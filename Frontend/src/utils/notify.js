import { toast } from "react-toastify";

/**
 * Thin wrapper around react-toastify so the rest of the app uses a single
 * import surface. Mirrors the AlertBanner type vocabulary (success/error/info/warning).
 */
const notify = {
  success: (message, options) => message && toast.success(message, options),
  error:   (message, options) => message && toast.error(message, options),
  info:    (message, options) => message && toast.info(message, options),
  warning: (message, options) => message && toast.warning(message, options),
  /** Generic dispatch by type string — handy when migrating AlertBanner usages. */
  show: (type, message, options) => {
    if (!message) return;
    const fn = toast[type] || toast.info;
    fn(message, options);
  },
};

export default notify;
export { toast };
