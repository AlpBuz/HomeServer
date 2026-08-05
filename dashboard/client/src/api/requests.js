const BASE = "/api";

async function request(path, options = {}) {
  // calls the api and returns the data, no polling happens here
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
  let timer; // variable that stores the timer ID

  async function poll () { // the function that calls the fetch of the api endpoint and runs the loop
    try {
      const data = await fetchFunction();
      onData(data);
    }catch (error){
      console.log(error);
    }

    timer = setTimeout(poll, interval); // the timeout that recalls the poll function
  };

  poll();

  return () => clearTimeout(timer);
}



export const api = {
  getSystemInfo: () => request("/system/info", {method: "GET"}),
  getSystemMetrics: () => request("/system/metrics", {method: "GET"}),
  getApplicationContainers: () => request("/docker/getApplications", {method: "GET"}),
  startContainer: (id) => request(`/docker/${id}/start`, { method: "POST" }),
  stopContainer: (id) => request(`/docker/${id}/stop`, { method: "POST" }),
  restartContainer: (id) => request(`/docker/${id}/restart`, { method: "POST" }),
  getRedirect: (id) => request(`/redirect/${id}`),
};