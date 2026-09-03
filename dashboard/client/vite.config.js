import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Vite's dev server rejects requests whose Host header isn't localhost by
    // default. This app is only ever reached through the Nginx reverse proxy
    // (LAN IP or a custom domain), so that default would block every user.
    allowedHosts: true,
  },
})
