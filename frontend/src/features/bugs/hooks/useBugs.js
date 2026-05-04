import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { bugKeys } from '@shared/api/queryKeys.js';
import { bugsApi } from '../api/bugsApi.js';

const EMPTY_PAGE = {
  content: [],
  totalElements: 0,
  totalPages: 0,
  size: 10,
  number: 0,
  first: true,
  last: true,
};

function normalizeFilters(filters) {
  return Object.keys(filters)
    .sort()
    .reduce((normalized, key) => ({ ...normalized, [key]: filters[key] }), {});
}

function getPageInfo(pageData) {
  return {
    totalElements: pageData?.totalElements ?? 0,
    totalPages: pageData?.totalPages ?? 0,
    size: pageData?.size ?? 10,
    number: pageData?.number ?? 0,
    first: pageData?.first ?? true,
    last: pageData?.last ?? true,
  };
}

async function loadBugs(filters) {
  const result = await bugsApi.getBugs(filters);

  if (!result.success) {
    throw new Error(result.error || 'Could not load bugs.');
  }

  return result.data ?? EMPTY_PAGE;
}

export function useBugs(filters) {
  const normalizedFilters = useMemo(() => normalizeFilters(filters), [filters]);

  const query = useQuery({
    queryKey: bugKeys.list(normalizedFilters),
    queryFn: () => loadBugs(normalizedFilters),
    staleTime: 30_000,
    placeholderData: (previousData) => previousData,
  });

  const pageData = query.data ?? EMPTY_PAGE;

  return {
    bugs: pageData.content ?? [],
    pageInfo: getPageInfo(pageData),
    isLoading: query.isLoading || query.isPlaceholderData,
    isFetching: query.isFetching,
    errorMessage: query.error?.message ?? '',
    reload: query.refetch,
  };
}
