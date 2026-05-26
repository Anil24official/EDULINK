import { useEffect, useRef } from "react";
import notify from "../../utils/notify";

/**
 * Drop-in replacement for the old inline AlertBanner.
 *
 * Backwards compatible: every existing caller still passes
 *   <AlertBanner type="success" message={msg} onClose={() => setMsg("")} />
 * but instead of rendering a top-of-section banner, we fire a toast.
 *
 * Behavior:
 *   - Toast is fired whenever `message` becomes truthy or changes to a new truthy value.
 *   - Empty / null / undefined message → nothing happens (matches the old early-return).
 *   - After dismissal we call `onClose` so the parent can reset its message state.
 *   - Renders nothing in the DOM, so existing layouts collapse the space gracefully.
 */
export default function AlertBanner({ type = "info", message, onClose }) {
  const lastShown = useRef(null);

  useEffect(() => {
    if (!message) return;
    // Avoid re-firing for the exact same message on every re-render.
    if (lastShown.current === message) return;
    lastShown.current = message;

    notify.show(type, message, {
      onClose: () => {
        if (typeof onClose === "function") onClose();
      },
    });
  }, [type, message, onClose]);

  return null;
}
