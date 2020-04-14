const auth = {
	getCred(callback) {
		const cred = {
			token: window.localStorage.getItem('token'),
			uid: window.localStorage.getItem('userid'),
			username: window.localStorage.getItem('username'),
		};
		setTimeout(callback, 100);
		return cred.token && cred.uid ? cred : null;
	},
	storeCred(cred, username, callback) {
		window.localStorage.setItem('userid', cred[0]);
		window.localStorage.setItem('token', cred[1]);
		window.localStorage.setItem('username', username);
		// remove below???
		//window.localStorage.setItem('token', 1);
		//window.localStorage.setItem('userid', 1);
		setTimeout(callback, 100);
	},
	logout(callback) {
		window.localStorage.removeItem('token');
		window.localStorage.removeItem('userid');
		setTimeout(callback, 100);
	},
};

export default auth;
