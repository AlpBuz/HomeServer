import { useState, useEffect } from 'react'
import { api } from '../api/requests'

export function useSystemInfo() {
  const [serverInfo, setServerInfo] = useState({});
  const [error, setError] = useState(false);

  useEffect(() => {
    async function fetchData() {
      try {
        const response = await api.getSystemInfo();
        setServerInfo(response);
      } catch (err) {
        console.error(err);
        setError(true);
      }
    }
    fetchData();
  }, []);

  return { serverInfo, error };
}