import * as api from '../api/api';
const redirectLogin = () => {
  window.vue.$router.replace('/login');
  window.localStorage.clear()

}

const hasLogin = () => {
  return window.sessionStorage.getItem('currentUser') !== null
}

const handleLogout = () => {
  api.logout();
  window.sessionStorage.removeItem("currentUser");
}

export {
  redirectLogin,
  hasLogin,
  handleLogout
}
