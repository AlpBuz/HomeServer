import { useState, useEffect, useCallback, useRef } from 'react'
import { api, pollingFunction } from '../api/requests'

export function getContainers() {
  const [containers, setContainers] = useState([]);
  const [error, setError] = useState(false);

  // holds the "stop polling" function, same pattern as before
  const stopPollingRef = useRef(null);

  const fetchContainers = useCallback(async () => {
    try {
      const data = await api.getApplicationContainers();
      setContainers(data);
      setError(false);
    } catch (err) {
      console.error(err);
      setError(true);
      stopPollingRef.current?.();
    }
  }, []);

  // start polling on mount, clean up on unmount
  useEffect(() => {
    stopPollingRef.current = pollingFunction(fetchContainers, () => {}, 5000);
    return () => stopPollingRef.current?.();
  }, [fetchContainers]);

  // exposed so a retry button can call it
  const retry = useCallback(() => {
    stopPollingRef.current?.();
    fetchContainers(); // immediate refetch, don't wait for next interval
    stopPollingRef.current = pollingFunction(fetchContainers, () => {}, 5000);
  }, [fetchContainers]);

  return { containers, error, retry };
}