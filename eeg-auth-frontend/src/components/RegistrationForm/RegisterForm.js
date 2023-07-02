import React, { useState } from "react";
import "./RegisterForm.css";
import { CountdownCircleTimer } from "react-countdown-circle-timer";
import "../Auth/Auth.css";
import { Link } from "react-router-dom";

const apiURL = process.env.REACT_APP_API_URL;
const TIMER_DURATION = 180; //180 seconds = 3 minutes

const buttonLabel = "Already registered?";

// Displays registration status
function SuccessfulRegistration() {
	return (
		<>
			<div className="registerCard">
				<label className="statusLabel">Successfully Registered!</label>
			</div>
			<div className="button-container">
				<Link to="/login">
					<button>Go to Login</button>
				</Link>
			</div>
		</>
	);
}

// Handles the click of the Retry button
function handleRetry() {
	window.location.reload();
}

// Displays message abd button when registration fails
function UnsuccessfulRegistration() {
	return (
		<>
			<div className="registerCard">
				<label className="statusLabel">Registration Failed!</label>
			</div>
			<div className="button-container">
				<button onClick={handleRetry}>{"Try again"}</button>
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
				<button onClick={handleRetry}>{"Try again"}</button>
			</div>
		</>
	);
}

// Displays the countdown timer component
function PleaseWait({ registrationStatus, hasResponded }) {
	const [isRegistered, setIsRegistered] = useState(false);

	return (
		<>
			{!isRegistered ? (
				<div className="registerCard">
					<label className="connectionLabel">Recording Brain Activity...</label>
					<br></br>
					<CountdownCircleTimer
						isPlaying={true}
						duration={TIMER_DURATION}
						colors={["#007aff", "#007aff", "#007aff"]}
						strokeWidth={4}
						size={70}
						onComplete={() => setIsRegistered(true)}
					>
						{({ remainingTime }) => remainingTime}
					</CountdownCircleTimer>
				</div>
			) : (
				<>
					{hasResponded ? (
						<div className="registerCard">
							<label className="statusLabel">
								{registrationStatus
									? SuccessfulRegistration()
									: UnsuccessfulRegistration()}
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

// Displays the registration form
function RegisterForm() {
	const [username, setUsername] = useState("");
	const [showNextComponent, setShowNextComponent] = useState(false);
	const [showPleaseWait, setShowPleaseWait] = useState(false);
	const [usernameError, setUsernameError] = useState("");
	const [showRedirect, setShowRedirect] = useState(true);
	const [registrationStatus, setRegistrationStatus] = useState(false);
	const [hasResponded, setHasResponded] = useState(false);
	const [showError, setShowError] = useState(false);

	const handleSubmit = (event) => {
		event.preventDefault();
		if (username.trim() === "") {
			setUsernameError("*Username cannot be empty");
		} else {
			setShowRedirect(false);
			setShowPleaseWait(true);
			setShowNextComponent(true);

			fetch(`${apiURL}/users/register/${username}`, {
				method: "POST",
				body: JSON.stringify({}),
				headers: {
					"Content-Type": "application/json",
				},
			})
				.then((response) => {
					if (response.status === 201) {
						setRegistrationStatus(true);
					} else {
						setRegistrationStatus(false);
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
				<label className="registerLabel">Register</label>
				{!showNextComponent && (
					<form className="registerCard" onSubmit={handleSubmit}>
						<fieldset>
							<legend>Select Username:</legend>
							<input
								type="text"
								value={username}
								onChange={handleUsernameChange}
							/>
						</fieldset>
						{usernameError && (
							<div className="usernameErrorLabel">{usernameError}</div>
						)}
						<button type="submit"> Next</button>
					</form>
				)}
				{!showError ? (
					<>
						{showPleaseWait && (
							<PleaseWait
								registrationStatus={registrationStatus}
								hasResponded={hasResponded}
							/>
						)}
						{showRedirect && (
							<div className="button-container">
								<p className="button-label">{buttonLabel}</p>
								<Link to="/login">
									<button>Go to Login</button>
								</Link>
							</div>
						)}
					</>
				) : (
					<SomethingWentWrong />
				)}
			</div>
		</>
	);
}

export default RegisterForm;
