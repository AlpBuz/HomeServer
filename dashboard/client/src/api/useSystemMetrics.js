import { useState, useEffect, useCallback, useRef } from 'react'
import { api, pollingFunction } from './requests'

export function useSystemMetrics() {
  const [metricsInfo, setMetricsInfo] = useState({});
  const [error, setError] = useState(false);

  const stopPollingRef = useRef(null);

  useEffect(() => {
    stopPollingRef.current = pollingFunction(api.getSystemMetrics, setMetricsInfo, 5000);
    return () => stopPollingRef.current?.();
  }, []);

  const retry = useCallback(() => {
    stopPollingRef.current?.();
    setMetricsInfo(fetchMetrics());
    stopPollingRef.current = pollingFunction(api.getSystemMetrics, setMetricsInfo, 5000);
  }, []);

  return { metricsInfo, error, retry };
}