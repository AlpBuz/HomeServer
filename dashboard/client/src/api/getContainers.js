import { useState, useEffect, useCallback, useRef } from 'react'
import { api, pollingFunction } from '../api/requests'

export function getContainers() {
  const [containers, setContainers] = useState([]);
  const [error, setError] = useState(false);

  // holds the "stop polling" function, same pattern as before
  const stopPollingRef = useRef(null);


  // start polling on mount, clean up on unmount
  useEffect(() => {
    stopPollingRef.current = pollingFunction(api.getApplicationContainers, setContainers, 5000);
    return () => stopPollingRef.current?.();
  }, []);

  // exposed so a retry button can call it
  const retry = useCallback(() => {
    stopPollingRef.current?.();
    setContainers(api.getApplicationContainers()); // immediate refetch, don't wait for next interval
    stopPollingRef.current = pollingFunction(api.getApplicationContainers, setContainers, 5000);
  }, []);

  return { containers, error, retry };
}