document.addEventListener("DOMContentLoaded", () => {
    const username = localStorage.getItem("username");
    const role = localStorage.getItem("role");
    const userId = localStorage.getItem("userId");

    if (!userId) {
        alert("Not logged in");
        window.location.href = "index.html";
        return;
    }

    if (role === "PLAYER") {
        document.getElementById("playerMatchesSection").style.display = "block";
        document.getElementById("joinedTournamentsSection").style.display = "block";
        fetchPlayerMatches(userId);
        fetchJoinedTournaments(userId);
    }

    if (role === "REFEREE") {
        document.getElementById("refereeMatchesSection").style.display = "block";
        fetchRefereeMatches(userId);
        fetchCompletedRefereeMatches(userId); // 👈 add this
    }


    if (role === "ADMIN") {
        document.getElementById("adminSection").style.display = "block";
        fetchPendingRequests();
    }

    document.getElementById("username").textContent = username;
    document.getElementById("role").textContent = role;

    fetchTournaments(userId);
});

async function fetchTournaments(userId) {
    const res = await fetch("/tournaments");
    const data = await res.json();

    const listDiv = document.getElementById("tournamentList");
    listDiv.innerHTML = "";

    data.forEach(t => {
        const div = document.createElement("div");
        div.innerHTML = `
      <strong>${t.name}</strong> (ID: ${t.id})
      <button onclick="joinTournament(${t.id}, ${userId})">Join</button>
    `;
        listDiv.appendChild(div);
    });
}

async function joinTournament(tournamentId, userId) {
    const res = await fetch(`/registration-requests/tournament/${tournamentId}/player/${userId}`, {
        method: "POST"
    });

    if (res.ok) {
        alert("✅ Request submitted – waiting for admin approval.");
    } else {
        const msg = await res.text();
        alert("❌ " + msg);
    }
}


function logout() {
    localStorage.clear();
    window.location.href = "index.html";
}

async function fetchPlayerMatches(userId) {
    const res = await fetch(`/matches/player/${userId}`);
    const data = await res.json();

    const list = document.getElementById("playerMatchList");
    list.innerHTML = "";

    data.forEach(m => {
        const div = document.createElement("div");
        div.innerHTML = `
      <strong>Match #${m.id}</strong><br>
      🆚 ${m.player1.username} vs ${m.player2.username}<br>
      🧑‍⚖️ Referee: ${m.referee.username}<br>
      🏆 Score: ${m.score}<br>
      ✅ Completed: ${m.completed ? "Yes" : "No"}
      <hr>
    `;
        list.appendChild(div);
    });
}

async function fetchRefereeMatches(userId) {
    const res = await fetch(`/matches/referee/${userId}/incomplete`);
    const data = await res.json();

    const list = document.getElementById("refereeMatchList");
    list.innerHTML = "";

    data.forEach(m => {
        const div = document.createElement("div");
        div.innerHTML = `
      <strong>Match #${m.id}</strong><br>
      🆚 ${m.player1.username} vs ${m.player2.username}<br>
      🏟️ Tournament: ${m.tournament.name} (${m.tournament.location})<br>
      <input type="text" id="scoreInput-${m.id}" placeholder="Enter score" />
      <button onclick="submitScore(${m.id})">Submit Score</button>
      <hr>
    `;
        list.appendChild(div);
    });
}


async function submitScore(matchId) {
    const input = document.getElementById(`scoreInput-${matchId}`);
    const score = input.value;
    const refereeId = localStorage.getItem("userId");

    if (!score.trim()) {
        alert("Please enter a score.");
        return;
    }

    const res = await fetch(`/matches/${matchId}/score`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            refereeId: parseInt(refereeId),
            score: score,
            completed: true
        })
    });

    if (res.ok) {
        alert("✅ Score submitted!");
        fetchRefereeMatches(refereeId); // refresh the list
    } else {
        alert("❌ Failed to submit score.");
    }
}

async function createTournament() {
    const name = document.getElementById("newTournamentName").value;
    const location = document.getElementById("newTournamentLocation").value;
    const date = document.getElementById("newTournamentDate").value;

    if (!name || !location || !date) {
        alert("Please fill in all tournament fields.");
        return;
    }

    const res = await fetch("/tournaments/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ name, location, date })
    });

    if (res.ok) {
        alert("✅ Tournament created!");
        fetchTournaments(localStorage.getItem("userId")); // refresh list
    } else {
        alert("❌ Failed to create tournament.");
    }
}

async function exportFile(url, filename) {
    try {
        const res = await fetch(url);

        if (!res.ok) {
            const message = await res.text();
            alert("❌ " + message);
            return;
        }

        const blob = await res.blob();
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        link.remove();
    } catch (err) {
        alert("❌ Error: " + err.message);
    }
}

async function fetchAllUsers() {
    const res = await fetch("/users");
    const users = await res.json();

    const list = document.getElementById("userList");
    list.innerHTML = "";

    users.forEach(user => {
        const div = document.createElement("div");
        div.innerHTML = `
        👤 ID: ${user.id} | Username: ${user.username} | Email: ${user.email} | Role: ${user.role}
        `;
        list.appendChild(div);
    });
}


function exportMatchesCSV() {
    const id = document.getElementById("exportTournamentId").value;
    if (!id) return alert("Enter tournament ID");
    exportFile(`/tournaments/${id}/export-matches/csv`, `matches_tournament_${id}.csv`);
}

function exportMatchesTXT() {
    const id = document.getElementById("exportTournamentId").value;
    if (!id) return alert("Enter tournament ID");
    exportFile(`/tournaments/${id}/export-matches/txt`, `matches_tournament_${id}.txt`);
}

function exportPlayersCSV() {
    const id = document.getElementById("exportPlayerTournamentId").value;
    if (!id) return alert("Enter tournament ID");
    exportFile(`/tournaments/${id}/export-players`, `players_tournament_${id}.csv`);
}

function exportPlayersTXT() {
    const id = document.getElementById("exportPlayerTournamentId").value;
    if (!id) return alert("Enter tournament ID");
    exportFile(`/tournaments/${id}/export-players/txt`, `players_tournament_${id}.txt`);
}

async function updateUser() {
    const id = document.getElementById("updateUserId").value;
    const username = document.getElementById("adminUpdateUsername").value;
    const email = document.getElementById("adminUpdateEmail").value;
    const role = document.getElementById("adminUpdateRole").value;
    const password = document.getElementById("adminUpdatePassword").value;

    if (!id || !username || !email || !role || !password) {
        alert("❌ Please fill in all fields.");
        return;
    }

    const res = await fetch(`/users/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ id, username, email, role, password })
    });

    if (res.ok) {
        alert("✅ User updated!");
        fetchAllUsers(); // Refresh the list
    } else {
        const msg = await res.text();
        alert("❌ " + msg);
    }
}


async function deleteUser() {
    const id = document.getElementById("deleteUserId").value;

    if (!id) {
        alert("Please enter a user ID.");
        return;
    }

    const confirmed = confirm("Are you sure you want to delete this user?");
    if (!confirmed) return;

    const res = await fetch(`/users/${id}`, {
        method: "DELETE"
    });

    if (res.ok) {
        alert("✅ User deleted!");
        fetchAllUsers(); // Refresh the list
    } else {
        const msg = await res.text();
        alert("❌ " + msg);
    }
}

async function fetchJoinedTournaments(userId) {
    try {
        const res = await fetch(`/users/${userId}/tournaments`);
        if (!res.ok) throw new Error("Failed to load tournaments.");
        const tournaments = await res.json();

        const list = document.getElementById("joinedTournamentList");
        list.innerHTML = "";

        tournaments.forEach(t => {
            const div = document.createElement("div");
            div.innerHTML = `<strong>${t.name}</strong> — ${t.location} (${t.date})`;
            list.appendChild(div);
        });
    } catch (err) {
        alert("❌ Error loading joined tournaments.");
    }
}

async function fetchCompletedRefereeMatches(userId) {
    const res = await fetch(`/matches/referee/${userId}/completed`);
    const data = await res.json();

    const list = document.getElementById("refereeCompletedMatchList");
    list.innerHTML = "";

    data.forEach(m => {
        const div = document.createElement("div");
        div.innerHTML = `
      <strong>Match #${m.id}</strong><br>
      🆚 ${m.player1.username} vs ${m.player2.username}<br>
      🏟️ Tournament: ${m.tournament.name} (${m.tournament.location})<br>
      🏆 Score: ${m.score}<br>
      ✅ Completed
      <hr>
    `;
        list.appendChild(div);
    });
}


async function updateAccount() {
    const userId = localStorage.getItem("userId");
    const username = document.getElementById("updateUsername").value;
    const email = document.getElementById("updateEmail").value;
    const password = document.getElementById("updatePassword").value;

    if (!username || !email || !password) {
        alert("Please fill in all fields.");
        return;
    }

    const res = await fetch(`/users/${userId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, email, password })
    });

    if (res.ok) {
        alert("✅ Account updated successfully!");
        localStorage.setItem("username", username);
        document.getElementById("username").textContent = username;
    } else {
        const error = await res.text();
        alert("❌ Failed to update: " + error);
    }
}

/* ---------- Pending registration handling (ADMIN) ---------- */

async function fetchPendingRequests() {
    const listDiv = document.getElementById("pendingRequestList");
    listDiv.innerHTML = "Loading…";

    const res = await fetch("/registration-requests");
    if (!res.ok) {
        listDiv.textContent = "Failed to load.";
        return;
    }

    const requests = await res.json();
    if (!requests.length) {
        listDiv.textContent = "No pending requests 🎉";
        return;
    }

    // Build list
    listDiv.innerHTML = "";
    requests.forEach(r => {
        const div = document.createElement("div");
        div.style.border = "1px solid #ccc";
        div.style.padding = "6px";
        div.style.marginBottom = "6px";

        div.innerHTML = `
          <strong>Req #${r.id}</strong> — 
          Player: ${r.player.username} (ID ${r.player.id}) → 
          Tournament: ${r.tournament.name} (ID ${r.tournament.id})
          <br>
          <button onclick="decideRequest(${r.id}, true)">Approve</button>
          <button onclick="decideRequest(${r.id}, false)">Deny</button>
        `;
        listDiv.appendChild(div);
    });
}

async function decideRequest(reqId, approve) {
    const url = `/registration-requests/${reqId}/${approve ? "approve" : "deny"}`;
    const res = await fetch(url, { method: "PATCH" });

    if (res.ok) {
        alert(approve ? "✅ Approved!" : "❌ Denied.");
        fetchPendingRequests();           // refresh list
    } else {
        const msg = await res.text();
        alert("Error: " + msg);
    }
}

