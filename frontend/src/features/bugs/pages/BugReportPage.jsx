import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '@features/auth/hooks/useAuth.js';
import { bugsApi } from '../api.js';
import './BugReportPage.css';

const EMPTY_FORM = {
  title: '',
  text: '',
  picture: '',
};

function normalizeTagName(value) {
  return value.trim().replace(/^#/, '').toLowerCase();
}

function BugReportPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const isEditMode = Boolean(id);
  const [form, setForm] = useState(EMPTY_FORM);
  const [tags, setTags] = useState([]);
  const [selectedTagIds, setSelectedTagIds] = useState([]);
  const [selectedExistingTagId, setSelectedExistingTagId] = useState('');
  const [newTagName, setNewTagName] = useState('');
  const [isLoading, setIsLoading] = useState(isEditMode);
  const [isSaving, setIsSaving] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    let cancelled = false;

    async function loadInitialData() {
      setIsLoading(true);
      setMessage('');

      const [tagsResult, bugResult, bugTagsResult] = await Promise.all([
        bugsApi.getTags(),
        isEditMode ? bugsApi.getBugById(id) : Promise.resolve(null),
        isEditMode ? bugsApi.getBugTags(id) : Promise.resolve(null),
      ]);

      if (cancelled) return;

      if (tagsResult.success) {
        setTags((tagsResult.data ?? []).sort((a, b) => a.name.localeCompare(b.name)));
      }

      if (isEditMode) {
        if (!bugResult?.success) {
          setMessage(bugResult?.error || 'Could not load this bug.');
          setIsLoading(false);
          return;
        }

        const bug = bugResult.data;
        setForm({
          title: bug.title ?? '',
          text: bug.text ?? '',
          picture: bug.picture ?? '',
        });

        if (bugTagsResult?.success) {
          setSelectedTagIds((bugTagsResult.data ?? []).map((tag) => tag.id));
        }
      }

      setIsLoading(false);
    }

    loadInitialData();

    return () => {
      cancelled = true;
    };
  }, [id, isEditMode]);

  const selectedTags = useMemo(
    () => tags.filter((tag) => selectedTagIds.some((tagId) => String(tagId) === String(tag.id))),
    [selectedTagIds, tags],
  );

  function updateField(field, value) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function addExistingTag() {
    if (!selectedExistingTagId) return;
    setSelectedTagIds((current) => {
      if (current.some((tagId) => String(tagId) === String(selectedExistingTagId))) {
        return current;
      }
      return [...current, Number(selectedExistingTagId)];
    });
    setSelectedExistingTagId('');
  }

  async function addNewTag() {
    const name = normalizeTagName(newTagName);
    if (!name) return;

    const existingTag = tags.find((tag) => tag.name.toLowerCase() === name);
    if (existingTag) {
      setSelectedTagIds((current) =>
        current.some((tagId) => String(tagId) === String(existingTag.id))
          ? current
          : [...current, existingTag.id],
      );
      setNewTagName('');
      return;
    }

    const result = await bugsApi.createTag({ name });
    if (!result.success) {
      setMessage(result.error || 'Could not create tag.');
      return;
    }

    const createdTag = result.data;
    setTags((current) => [...current, createdTag].sort((a, b) => a.name.localeCompare(b.name)));
    setSelectedTagIds((current) => [...current, createdTag.id]);
    setNewTagName('');
  }

  function removeSelectedTag(tagId) {
    setSelectedTagIds((current) => current.filter((item) => String(item) !== String(tagId)));
  }

  async function syncBugTags(bugId) {
    if (isEditMode) {
      const removeResult = await bugsApi.removeAllBugTags(bugId);
      if (!removeResult.success && removeResult.status !== 204) {
        throw new Error(removeResult.error || 'Could not update bug tags.');
      }
    }

    const results = await Promise.all(
      selectedTagIds.map((tagId) => bugsApi.addTagToBug(bugId, tagId)),
    );
    const failed = results.find((result) => !result.success && result.status !== 201);
    if (failed) {
      throw new Error(failed.error || 'Could not attach all selected tags.');
    }
  }

  async function handleSubmit(event) {
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

    setIsSaving(true);

    try {
      const payload = {
        title: form.title.trim(),
        text: form.text.trim(),
        picture: form.picture.trim() || null,
        ...(!isEditMode ? { author: { id: user.id } } : {}),
      };

      const result = isEditMode
        ? await bugsApi.updateBug(id, payload)
        : await bugsApi.createBug(payload);

      if (!result.success) {
        setMessage(result.error || 'Could not save bug.');
        setIsSaving(false);
        return;
      }

      await syncBugTags(result.data?.id ?? id);
      navigate('/bugs');
    } catch (error) {
      setMessage(error.message || 'Could not save bug.');
      setIsSaving(false);
    }
  }

  if (isLoading) {
    return <div className="bug-report-state">Loading report form...</div>;
  }

  return (
    <section className="bug-report-page">
      <header className="bug-report-header">
        <p>Bug reporting</p>
        <h1>{isEditMode ? 'Edit Bug Report' : 'Submit New Bug Report'}</h1>
        <span>Detail the technical issue so the engineering team can resolve it faster.</span>
      </header>

      <form className="bug-report-form" onSubmit={handleSubmit}>
        <label className="bug-report-field">
          <span>Bug title</span>
          <input
            value={form.title}
            onChange={(event) => updateField('title', event.target.value)}
            placeholder="e.g. Critical: Auth token expiration on checkout"
          />
        </label>

        <label className="bug-report-field bug-report-field--textarea">
          <span>Description</span>
          <textarea
            value={form.text}
            onChange={(event) => updateField('text', event.target.value)}
            placeholder="Steps to reproduce, expected vs actual behavior..."
          />
        </label>

        <div className="bug-report-lower">
          <label className="bug-report-field bug-report-field--url">
            <span>Screenshot / attachment URL</span>
            <input
              value={form.picture}
              onChange={(event) => updateField('picture', event.target.value)}
              placeholder="Paste image URL from database/storage..."
            />
          </label>

          <div className="bug-report-tags">
            <div className="bug-report-tags__header">
              <span>Tag management</span>
              <div>
                {selectedTags.map((tag) => (
                  <button key={tag.id} type="button" onClick={() => removeSelectedTag(tag.id)}>
                    {tag.name} x
                  </button>
                ))}
              </div>
            </div>

            <div className="bug-report-tags__controls">
              <select
                value={selectedExistingTagId}
                onChange={(event) => setSelectedExistingTagId(event.target.value)}
              >
                <option value="">Select existing tag...</option>
                {tags.map((tag) => (
                  <option key={tag.id} value={tag.id}>
                    {tag.name}
                  </option>
                ))}
              </select>
              <button type="button" onClick={addExistingTag}>
                Add
              </button>
            </div>

            <div className="bug-report-tags__controls">
              <input
                value={newTagName}
                onChange={(event) => setNewTagName(event.target.value)}
                placeholder="Create new tag..."
              />
              <button type="button" onClick={addNewTag}>
                Add New
              </button>
            </div>
          </div>
        </div>

        {message && <p className="bug-report-message">{message}</p>}

        <footer className="bug-report-actions">
          <button type="button" className="bug-report-secondary" onClick={() => navigate('/bugs')}>
            Cancel
          </button>
          <button type="submit" className="bug-report-primary" disabled={isSaving}>
            <span aria-hidden="true">▷</span>
            {isSaving ? 'Saving...' : isEditMode ? 'Save Changes' : 'Post Bug'}
          </button>
        </footer>
      </form>
    </section>
  );
}

export default BugReportPage;
