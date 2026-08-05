import { useState, useEffect, useCallback, useRef } from 'react'
import { api, pollingFunction } from './requests'

export function useSystemMetrics() {
  const [metricsInfo, setMetricsInfo] = useState({});
  const [error, setError] = useState(false);

  const stopPollingRef = useRef(null);

  const fetchMetrics = useCallback(async () => {
    try {
      const data = await api.getSystemMetrics();
      setMetricsInfo(data);
      setError(false);
    } catch (err) {
      console.error(err);
      setError(true);
      stopPollingRef.current?.();
    }
  }, []);

  useEffect(() => {
    stopPollingRef.current = pollingFunction(fetchMetrics, () => {}, 5000);
    return () => stopPollingRef.current?.();
  }, [fetchMetrics]);

  const retry = useCallback(() => {
    stopPollingRef.current?.();
    fetchMetrics();
    stopPollingRef.current = pollingFunction(fetchMetrics, () => {}, 5000);
  }, [fetchMetrics]);

  return { metricsInfo, error, retry };
}