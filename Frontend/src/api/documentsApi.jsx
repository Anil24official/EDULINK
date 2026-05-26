import httpClient from "./httpClient";
import Endpoints from "../config/endpoints";

const documentsApi = {
  /* ── School Admin: list and verify student documents (PDF 4.2) ── */
  adminList: (params) =>
    httpClient.get(Endpoints.documents.adminList, { params }),

  verify: (id, payload) =>
    httpClient.patch(Endpoints.documents.adminVerify(id), payload),
};

export default documentsApi;
