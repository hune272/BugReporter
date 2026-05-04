import { useMemo, useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { bugKeys } from '@shared/api/queryKeys.js';

const PAGE_SIZE = 10;

export function useBugFeedFilters({ userId }) {
  const queryClient = useQueryClient();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTagId, setSelectedTagId] = useState('all');
  const [isTagOpen, setIsTagOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState('all');
  const [isUserOpen, setIsUserOpen] = useState(false);
  const [userSearchTerm, setUserSearchTerm] = useState('');
  const [mineOnly, setMineOnly] = useState(false);
  const [page, setPage] = useState(0);

  const bugFilters = useMemo(() => {
    const filters = {};
    const title = searchTerm.trim();

    if (title) {
      filters.title = title;
    }

    if (selectedTagId !== 'all') {
      filters.tagId = selectedTagId;
    }

    if (mineOnly && userId) {
      filters.authorId = userId;
    } else if (selectedUserId !== 'all') {
      filters.authorId = selectedUserId;
    }

    filters.page = page;
    filters.size = PAGE_SIZE;

    return filters;
  }, [mineOnly, page, searchTerm, selectedTagId, selectedUserId, userId]);

  function getControlState(meta) {
    const selectedTag = meta.tags.find((tag) => String(tag.id) === String(selectedTagId));
    const selectedUser = meta.users.find((item) => String(item.id) === String(selectedUserId));
    const query = userSearchTerm.trim().toLowerCase();
    const visibleUsers = query
      ? meta.users.filter((item) =>
        [item.username, item.email].filter(Boolean).some((value) =>
          value.toLowerCase().includes(query),
        ),
      )
      : meta.users;

    return {
      selectedTagLabel: selectedTagId === 'all' ? 'All Tags' : selectedTag?.name ?? 'All Tags',
      selectedUserLabel: mineOnly
        ? 'My Bugs'
        : selectedUserId === 'all'
          ? 'All Users'
          : selectedUser?.username ?? 'All Users',
      visibleUsers,
    };
  }

  function selectTag(tagId) {
    setSelectedTagId(tagId);
    setIsTagOpen(false);
    setPage(0);
    queryClient.invalidateQueries({ queryKey: bugKeys.root });
  }

  function selectUser(userIdValue) {
    setSelectedUserId(userIdValue);
    setUserSearchTerm('');
    setIsUserOpen(false);
    setPage(0);
  }

  function updateMineOnly(isChecked) {
    setMineOnly(isChecked);
    setPage(0);
    if (isChecked) {
      setSelectedUserId('all');
      setIsUserOpen(false);
    }
  }

  function updateSearchTerm(value) {
    setSearchTerm(value);
    setPage(0);
  }

  return {
    searchTerm,
    selectedTagId,
    isTagOpen,
    selectedUserId,
    isUserOpen,
    userSearchTerm,
    mineOnly,
    bugFilters,
    setIsTagOpen,
    setIsUserOpen,
    setUserSearchTerm,
    setPage,
    selectTag,
    selectUser,
    updateMineOnly,
    updateSearchTerm,
    getControlState,
  };
}
