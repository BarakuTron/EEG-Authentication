import React from "react";
import "./Auth.css";
import { Link } from "react-router-dom";

const Auth = () => {
	return (
		<div className="auth-container">
			<br></br>
			<div className="card">
				<h2>EEG-based Authentication</h2>
				<br></br>
				<p className="selection-message">Please select an option:</p>
				<div className="button-container">
					<Link to="/login">
						<button className="btn">Log In</button>
					</Link>
					<br></br>
					<br></br>
					<Link to="/register">
						<button className="btn">Register</button>
					</Link>
				</div>
			</div>
		</div>
	);
};

export default Auth;
