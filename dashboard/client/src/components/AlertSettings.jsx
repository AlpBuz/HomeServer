import { useState, useEffect, useRef } from "react";
import { FaEnvelope } from "react-icons/fa";
import { api } from "../api/requests";
import "../style/AlertSettings.css";

// The API returns a plain Error for non-2xx responses whose message trails
// off with the JSON body (see api/requests.js) - pull the real reason out of it.
function extractErrorMessage(err, fallback) {
    const text = err?.message ?? "";
    const jsonStart = text.indexOf("{");
    if (jsonStart !== -1) {
        try {
            const parsed = JSON.parse(text.slice(jsonStart));
            if (parsed?.message) return parsed.message;
        } catch (_) {
            // fall through to the fallback below
        }
    }
    return fallback;
}

function AlertSettings() {
    const [open, setOpen] = useState(false);
    const [status, setStatus] = useState(null); // { configured, email }
    const [input, setInput] = useState("");
    const [message, setMessage] = useState(null); // { type: "success" | "error", text }
    const [loading, setLoading] = useState(false);
    const wrapperRef = useRef(null);

    async function loadStatus() {
        try {
            const data = await api.getAlertEmail();
            setStatus(data);
        } catch (err) {
            console.log(err);
        }
    }

    useEffect(() => {
        loadStatus();
    }, []);

    // close the popover on an outside click or Escape
    useEffect(() => {
        if (!open) return;

        function handleClick(e) {
            if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
                setOpen(false);
            }
        }
        function handleKey(e) {
            if (e.key === "Escape") setOpen(false);
        }

        document.addEventListener("mousedown", handleClick);
        document.addEventListener("keydown", handleKey);
        return () => {
            document.removeEventListener("mousedown", handleClick);
            document.removeEventListener("keydown", handleKey);
        };
    }, [open]);

    async function handleSave(e) {
        e.preventDefault();
        if (!input.trim()) return;

        setLoading(true);
        setMessage(null);
        try {
            const res = await api.setAlertEmail(input.trim());
            setMessage({ type: "success", text: res.message });
            setInput("");
            await loadStatus();
        } catch (err) {
            setMessage({ type: "error", text: extractErrorMessage(err, "Failed to save alert email") });
        } finally {
            setLoading(false);
        }
    }

    async function handleRemove() {
        setLoading(true);
        setMessage(null);
        try {
            await api.clearAlertEmail();
            setMessage({ type: "success", text: "Alert email removed" });
            await loadStatus();
        } catch (err) {
            setMessage({ type: "error", text: extractErrorMessage(err, "Failed to remove alert email") });
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="alert-settings-wrapper" ref={wrapperRef}>
            <button
                type="button"
                className={`alert-settings-trigger${status?.configured ? " configured" : ""}`}
                onClick={() => setOpen((o) => !o)}
                aria-label="Alert email settings"
                title="Alert email settings"
            >
                <FaEnvelope />
            </button>

            {open && (
                <div className="alert-settings-popover">
                    <p className="alert-settings-description">Get emailed when a container goes down.</p>

                    <p className="alert-settings-status">
                        {status?.configured ? (
                            <>Alerts go to <span className="alert-email-value">{status.email}</span></>
                        ) : (
                            "No alert email configured yet."
                        )}
                    </p>

                    <form className="alert-settings-form" onSubmit={handleSave}>
                        <input
                            type="email"
                            placeholder="you@example.com"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            required
                        />
                        <div className="alert-settings-actions">
                            <button type="submit" disabled={loading}>
                                {status?.configured ? "Update" : "Save"}
                            </button>
                            {status?.configured && (
                                <button type="button" className="alert-remove-button" onClick={handleRemove} disabled={loading}>
                                    Remove
                                </button>
                            )}
                        </div>
                    </form>

                    {message && <p className={`alert-settings-message ${message.type}`}>{message.text}</p>}
                </div>
            )}
        </div>
    );
}

export default AlertSettings;
