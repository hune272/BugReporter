import { useMutation, useQueryClient } from '@tanstack/react-query';
import { bugKeys, userKeys } from '@shared/api/queryKeys.js';
import { votesApi } from '../api/votesApi.js';

export function useVoteBug() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: votesApi.voteBug,
    onSuccess: (result) => {
      if (!result.success) return;
      queryClient.invalidateQueries({ queryKey: bugKeys.root });
      queryClient.invalidateQueries({ queryKey: userKeys.scores });
    },
  });
}
