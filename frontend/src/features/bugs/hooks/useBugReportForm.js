import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { bugKeys, tagKeys } from '@shared/api/queryKeys.js';
import { tagsApi } from '@features/tags/api/tagsApi.js';
import { bugsApi } from '../api/bugsApi.js';

const EMPTY_FORM = {
  title: '',
  text: '',
  picture: '',
};

function normalizeTagName(value) {
  return value.trim().replace(/^#/, '').toLowerCase();
}

async function loadTags() {
  const result = await tagsApi.getTags();
  if (!result.success) {
    throw new Error(result.error || 'Could not load tags.');
  }
  return result.data ?? [];
}

async function loadBug(id) {
  const result = await bugsApi.getBugById(id);
  if (!result.success) {
    throw new Error(result.error || 'Could not load this bug.');
  }
  return result.data;
}

export function useBugReportForm({ bugId, isEditMode, user, onSaved }) {
  const queryClient = useQueryClient();
  const [formDraft, setFormDraft] = useState(null);
  const [selectedTagIdsDraft, setSelectedTagIdsDraft] = useState(null);
  const [selectedExistingTagId, setSelectedExistingTagId] = useState('');
  const [isTagMenuOpen, setIsTagMenuOpen] = useState(false);
  const [newTagName, setNewTagName] = useState('');
  const [message, setMessage] = useState('');

  const tagsQuery = useQuery({
    queryKey: tagKeys.list,
    queryFn: loadTags,
    staleTime: 5 * 60_000,
  });

  const bugQuery = useQuery({
    queryKey: bugKeys.detail(bugId),
    queryFn: () => loadBug(bugId),
    enabled: isEditMode,
    staleTime: 30_000,
  });

  const createTagMutation = useMutation({
    mutationFn: tagsApi.createTag,
    onSuccess: (result) => {
      if (result.success && result.data) {
        queryClient.setQueryData(tagKeys.list, (current = []) => {
          if (current.some((tag) => String(tag.id) === String(result.data.id))) {
            return current;
          }
          return [...current, result.data];
        });
      }
      queryClient.invalidateQueries({ queryKey: tagKeys.root });
      queryClient.invalidateQueries({ queryKey: tagKeys.usage });
    },
  });

  const saveBugMutation = useMutation({
    mutationFn: ({ id, payload }) =>
      id ? bugsApi.updateBug(id, payload) : bugsApi.createBug(payload),
    onSuccess: (result, variables) => {
      if (!result.success) return;
      queryClient.invalidateQueries({ queryKey: bugKeys.root });
      queryClient.invalidateQueries({ queryKey: tagKeys.root });
      queryClient.invalidateQueries({ queryKey: tagKeys.usage });
      if (variables.id) {
        queryClient.invalidateQueries({ queryKey: bugKeys.detail(variables.id) });
      }
    },
  });

  const tags = useMemo(
    () => [...(tagsQuery.data ?? [])].sort((a, b) => a.name.localeCompare(b.name)),
    [tagsQuery.data],
  );

  const initialForm = useMemo(() => {
    if (!isEditMode || !bugQuery.data) {
      return EMPTY_FORM;
    }

    return {
      title: bugQuery.data.title ?? '',
      text: bugQuery.data.text ?? '',
      picture: bugQuery.data.picture ?? '',
    };
  }, [bugQuery.data, isEditMode]);

  const initialSelectedTagIds = useMemo(() => {
    if (!isEditMode || !bugQuery.data) {
      return [];
    }

    return (bugQuery.data.tags ?? []).map((tag) => tag.id);
  }, [bugQuery.data, isEditMode]);

  const form = formDraft ?? initialForm;
  const selectedTagIds = selectedTagIdsDraft ?? initialSelectedTagIds;
  const errorMessage =
    message ||
    tagsQuery.error?.message ||
    bugQuery.error?.message ||
    '';

  const selectedTags = useMemo(
    () => tags.filter((tag) => selectedTagIds.some((tagId) => String(tagId) === String(tag.id))),
    [selectedTagIds, tags],
  );

  const selectedExistingTag = tags.find((tag) => String(tag.id) === String(selectedExistingTagId));
  const isLoading = tagsQuery.isLoading || (isEditMode && bugQuery.isLoading);
  const isSaving = saveBugMutation.isPending || createTagMutation.isPending;

  function updateField(field, value) {
    setFormDraft((current) => ({ ...(current ?? form), [field]: value }));
  }

  function addExistingTag() {
    if (!selectedExistingTagId) return;
    setSelectedTagIdsDraft((current) => {
      const tagIds = current ?? selectedTagIds;
      if (tagIds.some((tagId) => String(tagId) === String(selectedExistingTagId))) {
        return tagIds;
      }
      return [...tagIds, Number(selectedExistingTagId)];
    });
    setSelectedExistingTagId('');
  }

  function selectExistingTag(tagId) {
    setSelectedExistingTagId(tagId);
    setIsTagMenuOpen(false);
  }

  async function addNewTag() {
    const name = normalizeTagName(newTagName);
    if (!name) return;

    const existingTag = tags.find((tag) => tag.name.toLowerCase() === name);
    if (existingTag) {
      setSelectedTagIdsDraft((current) => {
        const tagIds = current ?? selectedTagIds;
        return tagIds.some((tagId) => String(tagId) === String(existingTag.id))
          ? tagIds
          : [...tagIds, existingTag.id];
      });
      setNewTagName('');
      return;
    }

    const result = await createTagMutation.mutateAsync({ name });
    if (!result.success) {
      setMessage(result.error || 'Could not create tag.');
      return;
    }

    const createdTag = result.data;
    setSelectedTagIdsDraft((current) => [...(current ?? selectedTagIds), createdTag.id]);
    setNewTagName('');
    setIsTagMenuOpen(false);
    setSelectedExistingTagId('');
  }

  function removeSelectedTag(tagId) {
    setSelectedTagIdsDraft((current) =>
      (current ?? selectedTagIds).filter((item) => String(item) !== String(tagId)),
    );
  }

  async function submit(event) {
    event.preventDefault();
    setMessage('');

    if (!form.title.trim() || !form.text.trim()) {
      setMessage('Title and description are required.');
      return;
    }

    if (!isEditMode && !user?.id) {
      setMessage('You need a logged in user before reporting a bug.');
      return;
    }

    if (selectedTagIds.length === 0) {
      setMessage('Select at least one tag.');
      return;
    }

    try {
      const payload = {
        title: form.title.trim(),
        text: form.text.trim(),
        picture: form.picture.trim() || null,
        tagIds: selectedTagIds,
      };

      const result = await saveBugMutation.mutateAsync({
        id: isEditMode ? bugId : null,
        payload,
      });

      if (!result.success) {
        setMessage(result.error || 'Could not save bug.');
        return;
      }

      onSaved();
    } catch (error) {
      setMessage(error.message || 'Could not save bug.');
    }
  }

  return {
    form,
    tags,
    selectedTags,
    selectedExistingTag,
    selectedExistingTagId,
    isTagMenuOpen,
    newTagName,
    errorMessage,
    isLoading,
    isSaving,
    updateField,
    addExistingTag,
    selectExistingTag,
    addNewTag,
    removeSelectedTag,
    setNewTagName,
    toggleTagMenu: () => setIsTagMenuOpen((isOpen) => !isOpen),
    submit,
  };
}
