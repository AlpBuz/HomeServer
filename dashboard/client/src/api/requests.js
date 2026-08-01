const BASE = "/api";

async function request(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  if (!res.ok) {
    const body = await res.text().catch(() => "");
    throw new Error(`${options.method || "GET"} ${path} failed: ${res.status} ${body}`);
  }

  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

export function pollingFunction(fetchFunction, onData, interval = 5000) {
  let timer;

  const poll = async () => {
    try {
      const data = await fetchFunction();
      onData(data);
    } catch (err) {
      console.log(err);
    }

    timer = setTimeout(poll, interval);
  };

  poll();

  return () => clearTimeout(timer);
}



export const api = {
  getSystemInfo: () => request("/system/info", {method: "GET"}),
  getSystemMetrics: () => request("/system/metrics", {method: "GET"}),
  getContainers: () => request("/docker/getContainers", {method: "GET"}),
  startContainer: (id) => request(`/docker/${id}/start`, { method: "POST" }),
  stopContainer: (id) => request(`/docker/${id}/stop`, { method: "POST" }),
  restartContainer: (id) => request(`/docker/${id}/restart`, { method: "POST" }),
  getRedirect: (id) => request(`/redirect/${id}`),
};