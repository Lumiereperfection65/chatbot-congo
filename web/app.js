const API_URL = "http://localhost:8080/api";
const labels = {
  dashboard: ["VUE D’ENSEMBLE", "Vue d’ensemble", "Voici l’activité de votre plateforme aujourd’hui."],
  courses: ["FORMATION", "Mes cours", "Créez et organisez les formations de votre catalogue."],
  professors: ["ÉQUIPE PÉDAGOGIQUE", "Professeurs", "Gérez les profils de votre équipe pédagogique."],
  students: ["COMMUNAUTÉ", "Étudiants", "Retrouvez les étudiants inscrits sur la plateforme."]
};

const state = { token: localStorage.getItem("academie-token"), user: null, page: "dashboard", rows: [], search: "", editingId: null, toastTimer: null };
const $ = (selector) => document.querySelector(selector);

function headers(json = false) {
  const result = {};
  if (state.token) result.Authorization = `Bearer ${state.token}`;
  if (json) result["Content-Type"] = "application/json";
  return result;
}

async function api(path, options = {}) {
  let response;
  try {
    response = await fetch(`${API_URL}${path}`, {
      ...options,
      headers: { ...headers(Boolean(options.body)), ...(options.headers || {}) }
    });
  } catch {
    throw new Error("Impossible de joindre l’API. Vérifiez que le serveur Java est lancé sur le port 8080.");
  }
  const text = await response.text();
  let payload = null;
  if (text) {
    try { payload = JSON.parse(text); } catch { payload = text; }
  }
  if (response.status === 401 && path !== "/auth/login") {
    logout(false);
    throw new Error("Session expirée. Connectez-vous de nouveau.");
  }
  if (!response.ok) {
    throw new Error(payload && payload.error ? payload.error : `La requête a échoué (${response.status}).`);
  }
  return payload;
}

function showLogin() {
  $("#login-screen").classList.remove("hidden");
  $("#app-screen").classList.add("hidden");
}

function showApp(user) {
  state.user = user;
  $("#login-screen").classList.add("hidden");
  $("#app-screen").classList.remove("hidden");
  $("#user-name").textContent = user.nomComplet;
  $("#user-email").textContent = user.email;
  $("#avatar").textContent = `${user.prenom?.[0] || ""}${user.nom?.[0] || ""}`.toUpperCase();
  $("#today-label").textContent = new Intl.DateTimeFormat("fr-FR", { weekday: "short", day: "numeric", month: "short" }).format(new Date());
  openPage(state.page);
}

async function startSession() {
  if (!state.token) return showLogin();
  try {
    const user = await api("/auth/me");
    showApp(user);
  } catch {
    logout(false);
  }
}

function logout(showMessage = true) {
  if (state.token && showMessage) api("/auth/logout", { method: "POST" }).catch(() => {});
  state.token = null;
  state.user = null;
  localStorage.removeItem("academie-token");
  showLogin();
  if (showMessage) $("#login-error").textContent = "Vous êtes déconnecté.";
}

$("#login-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const button = event.currentTarget.querySelector("button[type=submit]");
  const error = $("#login-error");
  error.textContent = "";
  button.disabled = true;
  button.textContent = "Connexion…";
  try {
    const result = await api("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email: $("#login-email").value.trim(), password: $("#login-password").value })
    });
    state.token = result.token;
    localStorage.setItem("academie-token", state.token);
    showApp(result.user);
  } catch (problem) {
    error.textContent = problem.message;
  } finally {
    button.disabled = false;
    button.innerHTML = 'Ouvrir mon espace <span>→</span>';
  }
});

$("#logout-button").addEventListener("click", () => logout());
document.querySelectorAll(".nav-item").forEach((button) => button.addEventListener("click", () => openPage(button.dataset.page)));

async function openPage(page) {
  state.page = page;
  state.search = "";
  document.querySelectorAll(".nav-item").forEach((button) => button.classList.toggle("active", button.dataset.page === page));
  $("#breadcrumb-current").textContent = labels[page][1];
  const [eyebrow, title, description] = labels[page];
  const showAdd = page !== "dashboard";
  $("#page-heading").innerHTML = `<div><span class="eyebrow">${eyebrow}</span><h1>${title}</h1><p>${description}</p></div>${showAdd ? '<button id="add-button" class="button button-primary">＋ Ajouter</button>' : ""}`;
  $("#page-content").innerHTML = '<div class="empty-state">Chargement…</div>';
  $("#page-content").onclick = handleContentClick;
  $("#page-content").oninput = handleSearch;
  $("#add-button")?.addEventListener("click", () => openEditor(null));

  try {
    if (page === "dashboard") {
      const [summary, courses] = await Promise.all([api("/dashboard"), api("/courses")]);
      renderDashboard(summary, courses);
    } else {
      state.rows = await api(`/${page}`);
      renderTable();
    }
  } catch (problem) {
    $("#page-content").innerHTML = `<div class="empty-state">${escapeHtml(problem.message)}</div>`;
  }
}

function renderDashboard(summary, courses) {
  const cards = [
    ["Cours au catalogue", summary.cours, "▤"],
    ["Étudiants", summary.etudiants, "♧"],
    ["Professeurs", summary.professeurs, "♙"],
    ["Inscriptions", summary.inscriptions, "↗"]
  ];
  const sorted = [...courses].sort((a, b) => Number(b.id) - Number(a.id)).slice(0, 5);
  $("#page-content").innerHTML = `
    <section class="stats-grid">${cards.map(([label, value, icon]) => `<article class="stat-card"><div><label>${label}</label><strong>${value ?? 0}</strong></div><span class="stat-icon">${icon}</span></article>`).join("")}</section>
    <section class="dashboard-grid">
      <article class="panel"><div class="panel-head"><div><h2>Vos formations</h2><p>Un aperçu de votre catalogue de cours</p></div><button class="table-action" data-page-link="courses">Tout voir →</button></div>
        <div class="course-list">${sorted.length ? sorted.map((course) => `<div class="course-row"><div><strong>${escapeHtml(course.titre)}</strong><small>${escapeHtml(course.niveau || "—")} · ${escapeHtml(course.professeur || "Professeur à définir")}</small><div class="progress-track"><span style="width:${course.visible ? 76 : 28}%"></span></div></div><span class="course-count">${course.dureeHeures ?? "—"} h</span></div>`).join("") : '<div class="empty-state">Aucun cours pour le moment.</div>'}</div>
      </article>
      <article class="panel"><div class="panel-head"><div><h2>Accès rapide</h2><p>Les actions les plus courantes</p></div></div><div class="quick-actions">
        <button class="quick-action" data-page-link="courses"><span class="stat-icon">▤</span><span><strong>Gérer les cours</strong><small>Ajouter ou modifier une formation</small></span></button>
        <button class="quick-action" data-page-link="professors"><span class="stat-icon">♙</span><span><strong>Voir les professeurs</strong><small>Consulter l’équipe pédagogique</small></span></button>
        <button class="quick-action" data-page-link="students"><span class="stat-icon">♧</span><span><strong>Voir les étudiants</strong><small>Consulter la communauté</small></span></button>
      </div></article>
    </section>`;
}

function renderTable() {
  const page = state.page;
  const config = {
    courses: { columns: ["Formation", "Niveau", "Professeur", "Durée", "Prix", "Statut"], fields: ["titre", "niveau", "professeur", "dureeHeures", "prix", "statut"] },
    professors: { columns: ["Professeur", "E-mail", "Spécialité", "Téléphone"], fields: ["nomComplet", "email", "specialite", "telephone"] },
    students: { columns: ["Étudiant", "E-mail", "Niveau d’étude", "Téléphone"], fields: ["nomComplet", "email", "niveauEtude", "telephone"] }
  }[page];
  const filtered = state.rows.filter((row) => Object.values(row).some((value) => String(value ?? "").toLowerCase().includes(state.search.toLowerCase())));
  const body = filtered.map((row) => {
    const cells = config.fields.map((field, index) => {
      let value = row[field] ?? "—";
      if (field === "prix" && value !== "—") value = `${Number(value).toLocaleString("fr-FR")} €`;
      if (field === "dureeHeures" && value !== "—") value += " h";
      if (field === "statut") return `<td><span class="badge ${String(value).toLowerCase().includes("préparation") || String(value).toLowerCase().includes("brouillon") ? "draft" : ""}">${escapeHtml(value)}</span></td>`;
      if (index === 0) return `<td><div class="table-person"><span class="mini-avatar">${escapeHtml(initials(row.nomComplet || row.titre))}</span><strong>${escapeHtml(value)}</strong></div></td>`;
      return `<td>${escapeHtml(value)}</td>`;
    }).join("");
    return `<tr>${cells}<td><div class="actions-cell"><button class="table-action" data-edit="${row.id}">Modifier</button><button class="table-action delete" data-delete="${row.id}">Supprimer</button></div></td></tr>`;
  }).join("");
  $("#page-content").innerHTML = `<section class="table-panel">
    <div class="table-head"><h2>${filtered.length} résultat${filtered.length > 1 ? "s" : ""}</h2><div class="table-tools"><input id="table-search" class="search-input" type="search" placeholder="Rechercher…" value="${escapeHtml(state.search)}"><button id="table-add" class="button button-primary">＋ Ajouter</button></div></div>
    <div class="data-table-wrap"><table class="data-table"><thead><tr>${config.columns.map((column) => `<th>${column}</th>`).join("")}<th>Actions</th></tr></thead><tbody>${body}</tbody></table>${filtered.length ? "" : '<div class="empty-state">Aucun résultat trouvé.</div>'}</div>
  </section>`;
}

function handleSearch(event) {
  if (event.target.id !== "table-search") return;
  state.search = event.target.value;
  renderTable();
  const input = $("#table-search");
  input?.focus();
  if (input) input.setSelectionRange(state.search.length, state.search.length);
}

async function handleContentClick(event) {
  const pageLink = event.target.closest("[data-page-link]");
  if (pageLink) return openPage(pageLink.dataset.pageLink);
  if (event.target.id === "table-add") return openEditor(null);
  const edit = event.target.closest("[data-edit]");
  if (edit) return openEditor(state.rows.find((row) => String(row.id) === edit.dataset.edit));
  const remove = event.target.closest("[data-delete]");
  if (remove) {
    const row = state.rows.find((item) => String(item.id) === remove.dataset.delete);
    if (!row || !window.confirm(`Supprimer « ${row.nomComplet || row.titre} » ?`)) return;
    try {
      await api(`/${state.page}/${row.id}`, { method: "DELETE" });
      notify("Élément supprimé.");
      openPage(state.page);
    } catch (problem) { notify(problem.message, true); }
  }
}

const field = (name, label, type = "text", value = "", options = {}) => ({ name, label, type, value: value ?? "", ...options });
function fieldsFor(page, item) {
  if (page === "courses") {
    return [
      field("titre", "Titre du cours", "text", item?.titre, { required: true }),
      field("niveau", "Niveau", "select", item?.niveau || "DEBUTANT", { required: true, options: [["DEBUTANT", "Débutant"], ["INTERMEDIAIRE", "Intermédiaire"], ["AVANCE", "Avancé"]] }),
      field("presentation", "Présentation", "textarea", item?.presentation, { required: true, full: true }),
      field("motsCles", "Mots-clés", "text", item?.motsCles),
      field("publicVise", "Public visé", "text", item?.publicVise),
      field("prerequis", "Prérequis", "text", item?.prerequis),
      field("dureeHeures", "Durée (heures)", "number", item?.dureeHeures, { min: 1 }),
      field("capaciteMax", "Capacité maximale", "number", item?.capaciteMax ?? 30, { min: 1 }),
      field("prix", "Prix (€)", "number", item?.prix ?? 0, { min: 0, step: "0.01" }),
      field("professeurId", "Professeur", "select", item?.professeurId, { required: true, options: state.options.professeurs.map((p) => [p.id, p.nomComplet]) }),
      field("categorieId", "Catégorie", "select", item?.categorieId ?? "", { options: [["", "Aucune catégorie"], ...state.options.categories.map((category) => [category.id, category.nom])] }),
      field("dateDebut", "Date de début", "date", item?.dateDebut),
      field("dateFin", "Date de fin", "date", item?.dateFin),
      field("horaireDebut", "Horaire de début", "time", String(item?.horaireDebut || "").slice(0, 5)),
      field("horaireFin", "Horaire de fin", "time", String(item?.horaireFin || "").slice(0, 5)),
      field("joursSemaine", "Jours de cours", "text", item?.joursSemaine),
      field("salle", "Salle", "text", item?.salle),
      field("visible", "Cours visible", "checkbox", item?.visible ?? false, { full: true })
    ];
  }
  if (page === "professors") return [
    field("nom", "Nom", "text", item?.nom, { required: true }),
    field("prenom", "Prénom", "text", item?.prenom, { required: true }),
    field("email", "Adresse e-mail", "email", item?.email, { required: true }),
    field("telephone", "Téléphone", "tel", item?.telephone),
    field("specialite", "Spécialité", "text", item?.specialite, { required: true }),
    field("dateEmbauche", "Date d’embauche", "date", item?.dateEmbauche),
    field("salaire", "Salaire (€)", "number", item?.salaire, { min: 0, step: "0.01" })
  ];
  return [
    field("nom", "Nom", "text", item?.nom, { required: true }),
    field("prenom", "Prénom", "text", item?.prenom, { required: true }),
    field("email", "Adresse e-mail", "email", item?.email, { required: true }),
    field("telephone", "Téléphone", "tel", item?.telephone),
    field("dateNaissance", "Date de naissance", "date", item?.dateNaissance),
    field("niveauEtude", "Niveau d’étude", "text", item?.niveauEtude),
    field("adresse", "Adresse", "textarea", item?.adresse, { full: true })
  ];
}

const stateOptions = { professeurs: [], categories: [] };
Object.defineProperty(state, "options", { value: stateOptions });

async function openEditor(item) {
  state.editingId = item?.id ?? null;
  $("#dialog-title").textContent = `${item ? "Modifier" : "Ajouter"} ${state.page === "courses" ? "un cours" : state.page === "professors" ? "un professeur" : "un étudiant"}`;
  $("#dialog-error").textContent = "";
  if (state.page === "courses") {
    try {
      const result = await api("/options");
      state.options.professeurs = result.professeurs;
      state.options.categories = result.categories;
    } catch (problem) { notify(problem.message, true); return; }
  }
  $("#dialog-fields").innerHTML = fieldsFor(state.page, item).map(renderField).join("");
  $("#edit-dialog").showModal();
}

function renderField(definition) {
  const { name, label, type, value, required, full, options = [], min, step } = definition;
  const wrapper = `dialog-field${full ? " full" : ""}`;
  if (type === "checkbox") return `<label class="${wrapper} toggle-field"><input name="${name}" type="checkbox" ${value ? "checked" : ""}>${label}</label>`;
  let control;
  if (type === "select") control = `<select class="field-select" name="${name}" ${required ? "required" : ""}>${options.map(([optionValue, optionLabel]) => `<option value="${escapeHtml(optionValue)}" ${String(value) === String(optionValue) ? "selected" : ""}>${escapeHtml(optionLabel)}</option>`).join("")}</select>`;
  else if (type === "textarea") control = `<textarea class="field-textarea" name="${name}" ${required ? "required" : ""}>${escapeHtml(value)}</textarea>`;
  else control = `<input class="field-input" name="${name}" type="${type}" value="${escapeHtml(value)}" ${required ? "required" : ""} ${min !== undefined ? `min="${min}"` : ""} ${step ? `step="${step}"` : ""}>`;
  return `<div class="${wrapper}"><label class="field-label" for="field-${name}">${label}${required ? " *" : ""}</label>${control}</div>`;
}

document.querySelectorAll(".close-dialog").forEach((button) => button.addEventListener("click", () => $("#edit-dialog").close()));
$("#edit-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  const payload = {};
  for (const [key, value] of form.entries()) payload[key] = value;
  for (const input of event.currentTarget.querySelectorAll('input[type="checkbox"]')) payload[input.name] = input.checked;
  for (const key of ["dureeHeures", "capaciteMax", "professeurId", "categorieId"]) {
    if (payload[key] === "") payload[key] = null;
    else if (payload[key] !== undefined && payload[key] !== null) payload[key] = Number(payload[key]);
  }
  for (const key of ["prix", "salaire"]) if (payload[key] !== undefined && payload[key] !== "") payload[key] = Number(payload[key]);
  if (payload.categorieId === null) delete payload.categorieId;

  const path = `/${state.page}${state.editingId ? `/${state.editingId}` : ""}`;
  try {
    await api(path, { method: state.editingId ? "PUT" : "POST", body: JSON.stringify(payload) });
    $("#edit-dialog").close();
    notify(state.editingId ? "Modifications enregistrées." : "Élément ajouté.");
    openPage(state.page);
  } catch (problem) {
    $("#dialog-error").textContent = problem.message;
  }
});

function initials(text) { return String(text || "?").split(/\s+/).slice(0, 2).map((part) => part[0] || "").join("").toUpperCase(); }
function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (character) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  })[character]);
}
function notify(message, error = false) {
  const toast = $("#toast");
  toast.textContent = message;
  toast.classList.toggle("error", error);
  toast.classList.add("visible");
  clearTimeout(state.toastTimer);
  state.toastTimer = setTimeout(() => toast.classList.remove("visible"), 3200);
}

startSession();
