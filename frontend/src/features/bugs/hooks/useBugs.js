import { useCallback, useEffect, useState } from 'react';
import { bugsApi } from '../api.js';

export function useBugs(filters) {
    const [bugs, setBugs] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState('');
    const [reloadToken, setReloadToken] = useState(0);

    const reload = useCallback(() => {
        setReloadToken((token) => token + 1);
    }, []);

    useEffect(() => {
        let cancelled = false;

        async function load() {
            setIsLoading(true);
            setErrorMessage('');

            const result = await bugsApi.getBugs(filters);

            if (cancelled) return;

            if (result.success) {
                setBugs(result.data ?? []);
            } else {
                setErrorMessage(result.error || 'Could not load bugs.');
            }

            setIsLoading(false);
        }

        load();

        return () => {
            cancelled = true;
        };
    }, [filters, reloadToken]);

    return { bugs, isLoading, errorMessage, reload };
}
