import React, { useState } from "react";
import "./LoginForm.css";
import "../Auth/Auth.css";
import { Link } from "react-router-dom";
import { CountdownCircleTimer } from "react-countdown-circle-timer";

const apiURL = process.env.REACT_APP_API_URL;
const buttonLabel = "Not registered?";
const TIMER_DURATION = 60; // 60sec = 1min

function SuccessfulAuthentication() {
	return (
		<>
			<div className="loginCard">
				<label className="statusLabel">Successfully Logged In!</label>
			</div>
			<div className="button-container">
				<button onClick={HandleRetry}>Log out</button>
			</div>
		</>
	);
}

function UnsuccessfulAuthentication() {
	return (
		<>
			<div className="registerCard">
				<label className="statusLabel">Authentication Failed!</label>
			</div>
			<div className="button-container">
				<button onClick={HandleRetry}>{"Try again"}</button>
			</div>
		</>
	);
}

// Displays message when registration is processing
function Processing() {
	return (
		<>
			<div className="registerCard">
				<label className="statusLabel">Processing...</label>
			</div>
		</>
	);
}

// Displays error message when something goes wrong
function SomethingWentWrong() {
	return (
		<>
			<div className="registerCard">
				<label className="statusLabel">Something went wrong.</label>
			</div>
			<div className="button-container">
				<button onClick={HandleRetry}>{"Try again"}</button>
			</div>
		</>
	);
}

function HandleRetry() {
	window.location.reload();
}

// Displays the countdown timer component
function PleaseWait({ loginStatus, hasResponded }) {
	const [isLoggingIn, setIsLoggingIn] = useState(false);

	return (
		<>
			{!isLoggingIn ? (
				<div className="loginCard">
					<label className="connectionLabel">Recording Brain Activity...</label>
					<br></br>
					<CountdownCircleTimer
						isPlaying={true}
						duration={TIMER_DURATION}
						colors={["#007aff", "#007aff", "#007aff"]}
						strokeWidth={4}
						size={70}
						onComplete={() => setIsLoggingIn(true)}
					>
						{({ remainingTime }) => remainingTime}
					</CountdownCircleTimer>
				</div>
			) : (
				<>
					{hasResponded ? (
						<div className="loginCard">
							<label className="statusLabel">
								{loginStatus
									? SuccessfulAuthentication()
									: UnsuccessfulAuthentication()}
							</label>
						</div>
					) : (
						Processing()
					)}
				</>
			)}
		</>
	);
}

function LoginForm() {
	const [username, setUsername] = useState("");
	const [loginStatus, setLoginStatus] = useState(false);
	const [showNextComponent, setShowNextComponent] = useState(false);
	const [showError, setShowError] = useState(false);
	const [hasResponded, setHasResponded] = useState(false);
	const [usernameError, setUsernameError] = useState("");

	const handleSubmit = (event) => {
		event.preventDefault();

		if (username.trim() === "") {
			setUsernameError("*Username cannot be empty");
		} else {
			setShowNextComponent(true);
			// Call API to authenticate user with username
			fetch(`${apiURL}/users/login/${username}`, {
				method: "POST",
				body: JSON.stringify({}),
				headers: {
					"Content-Type": "application/json",
				},
			})
				.then((response) => {
					if (response.status === 200) {
						setLoginStatus(true);
					} else {
						setLoginStatus(false);
					}
					setHasResponded(true);
				})
				.catch((error) => {
					setShowError(true);
					setHasResponded(true);
				});
		}
	};

	const handleUsernameChange = (event) => {
		setUsername(event.target.value);
		setUsernameError("");
	};

	return (
		<>
			<div className="auth-container">
				<label className="loginLabel">Log In</label>
				{!showNextComponent ? (
					<>
						<form className="loginCard" onSubmit={handleSubmit}>
							<fieldset>
								<legend>Username:</legend>
								<input
									type="text"
									value={username}
									onChange={handleUsernameChange}
								/>
							</fieldset>
							{usernameError && (
								<div className="usernameErrorLabel">{usernameError}</div>
							)}
							<button onSubmit={handleSubmit} type="submit">
								Next
							</button>
						</form>

						<div className="button-container">
							<p className="button-label">{buttonLabel}</p>
							<Link to="/register">
								<button>Go to Register</button>
							</Link>
						</div>
					</>
				) : (
					<>
						{!showError ? (
							<PleaseWait
								loginStatus={loginStatus}
								hasResponded={hasResponded}
							/>
						) : (
							<SomethingWentWrong />
						)}
					</>
				)}
			</div>
		</>
	);
}

export default LoginForm;
