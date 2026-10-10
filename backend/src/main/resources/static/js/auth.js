// STUDENT360 — Authentication & Session Management Module
const Auth = {
  user: null,

  async login(email, password, role) {
    const res = await API.post('/auth/login', { email, password, role });
    this.user = res;
    localStorage.setItem('student360_user', JSON.stringify(res));
    return res;
  },

  async logout() {
    try {
      await API.post('/auth/logout');
    } catch (e) {
      // ignore logout errors
    }
    this.user = null;
    localStorage.removeItem('student360_user');
    window.location.href = '/index.html';
  },

  async checkSession() {
    try {
      const res = await API.get('/auth/me');
      this.user = res;
      localStorage.setItem('student360_user', JSON.stringify(res));
      return res;
    } catch (e) {
      this.user = null;
      localStorage.removeItem('student360_user');
      return null;
    }
  },

  getCurrentUser() {
    if (!this.user) {
      const cached = localStorage.getItem('student360_user');
      if (cached) {
        try { this.user = JSON.parse(cached); } catch (e) {}
      }
    }
    return this.user;
  },

  isAdmin() {
    const u = this.getCurrentUser();
    return u && u.role === 'ADMIN';
  },

  isTeacher() {
    const u = this.getCurrentUser();
    return u && u.role === 'TEACHER';
  },

  isStudent() {
    const u = this.getCurrentUser();
    return u && u.role === 'STUDENT';
  },

  applyRoleRestrictions() {
    const user = this.getCurrentUser();
    if (!user) return;

    // Elements with data-role attributes
    document.querySelectorAll('[data-role]').forEach(el => {
      const allowedRoles = el.getAttribute('data-role').split(',').map(r => r.trim());
      if (allowedRoles.includes(user.role)) {
        el.style.display = '';
      } else {
        el.style.display = 'none';
      }
    });

    // Update avatar and username
    const avatarEl = document.getElementById('user-avatar');
    const nameEl = document.getElementById('user-name');
    const roleEl = document.getElementById('user-role');

    if (avatarEl) {
      avatarEl.textContent = (user.fullName || user.email || 'U').charAt(0).toUpperCase();
    }
    if (nameEl) {
      nameEl.textContent = user.fullName || user.email;
    }
    if (roleEl) {
      roleEl.textContent = user.role;
    }
  }
};
