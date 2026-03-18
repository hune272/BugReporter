# BugReporter

A Java Spring Boot + React web application for bug reporting.

## Project Structure

```
BugReporter/
├── backend/    # Java Spring Boot API
└── frontend/   # React frontend
```

## Branches

| Branch  | Description           |
|---------|-----------------------|
| main    | Production-ready code |
| marius  | Marius's dev branch   |
| hunor   | Hunor's dev branch    |
| vasile  | Vasile's dev branch   |

---

## Cum folosim Git — Ghid pentru echipă

### Regula de bază
Fiecare lucrează **doar pe branch-ul său**. Codul ajunge pe `main` doar când funcționează și este testat.

---

### Fluxul de lucru zilnic

#### 1. Înainte de a începe să lucrezi — trage ultimele modificări
```bash
git checkout hunor        # treci pe branch-ul tău
git pull origin main      # ia ultimele modificări din main
```

#### 2. Lucrează și salvează progresul (commit)
```bash
git add .                         # adaugă fișierele modificate
git commit -m "descriere scurta"  # salvează modificările local
git push origin hunor             # trimite pe GitHub
```
> Fă commit-uri des, cu mesaje clare: `"add login form"`, `"fix password validation"`, etc.

#### 3. Când codul tău merge — îl pui pe main (merge)
```bash
git checkout main         # treci pe main
git pull origin main      # asigură-te că main e la zi
git merge hunor           # adaugi codul tău în main
git push origin main      # trimiți pe GitHub
```

#### 4. Revino pe branch-ul tău și continuă
```bash
git checkout hunor
```

---

### Rezumat vizual

```
main   ──────────────────────────●──────────────────────●──────
                                ↑                        ↑
hunor  ──●────●────●────────────┘        ●────●─────────┘
vasile         ────●────●────●───────────────────────────●─────
```

---

### Reguli importante

- **Nu lucra direct pe `main`** — niciodată
- **Pull înainte de merge** — evită conflicte
- **Testează înainte de a pune pe main** — codul de pe main trebuie să funcționeze întotdeauna
- **Mesaje de commit clare** — colegii tăi vor înțelege ce ai schimbat

---

### Dacă apare un conflict

Un conflict apare când doi oameni au modificat același fișier. Git îți va arăta asta:
```
<<<<<<< hunor
codul tău
=======
codul din main
>>>>>>> main
```
Alegi ce varianta păstrezi (sau combini), ștergi liniile cu `<<<<`, `====`, `>>>>`, apoi:
```bash
git add .
git commit -m "resolve merge conflict"
```

---

### Comenzi utile

| Comandă | Ce face |
|---|---|
| `git status` | vezi ce fișiere ai modificat |
| `git log --oneline` | istoricul commit-urilor |
| `git diff` | vezi exact ce ai schimbat |
| `git checkout -- .` | anulezi toate modificările nesalvate |
