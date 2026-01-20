import { useState } from "react";
import "../index.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = (e) => {
  e.preventDefault();

  if (!email.includes("@")) {
    setError("Please enter a valid email address");
    return;
  }

  setError("");
  setLoading(true);

  // placeholder for backend call
  setTimeout(() => {
    setLoading(false);
    setError("Invalid email or password");
  }, 1000);
};


  return (
    <main className="login-container">
      <h1>Sign in</h1>

      <button className="social google">
        Continue with Google
      </button>

      <button className="social github">
        Continue with GitHub
      </button>

      <div className="divider">OR</div>

      <form onSubmit={handleSubmit}>
        <label>Email</label>
        <input
          type="email"
          value={email}
          required
          onChange={(e) => setEmail(e.target.value)}
        />

        <label>Password</label>
        <input
          type="password"
          value={password}
          required
          onChange={(e) => setPassword(e.target.value)}
        />

        {error && (
          <p role="alert" className="error">
            {error}
          </p>
        )}

        <button type="submit" disabled={loading}>
          {loading ? "Signing in..." : "Sign In"}
        </button>
      </form>

      <div className="links">
        <button type="button" className="link-button">Forgot password?</button>
        <button type="button" className="link-button">Create account</button>
      </div>
    </main>
  );
}

export default Login;
