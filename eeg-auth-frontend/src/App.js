import "./App.css";
import React from "react";
import Auth from "./components/Auth/Auth";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import LoginForm from "./components/LoginForm/LoginForm";
import RegisterForm from "./components/RegistrationForm/RegisterForm";

function App() {
	return (
		<Router>
			<Routes>
				<Route exact path="/" element={<Auth />} />
				<Route exact path="/login" element={<LoginForm />} />
				<Route exact path="/register" element={<RegisterForm />} />
			</Routes>
		</Router>
	);
}

export default App;
