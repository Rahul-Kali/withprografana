import { useState } from "react";

const orderApiUrl = import.meta.env.VITE_ORDER_API_URL || "/api/order";

export default function App() {
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const placeOrder = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch(orderApiUrl, {
        method: "POST"
      });

      if (!response.ok) {
        throw new Error("Order request failed");
      }

      const data = await response.json();
      setResult(data);
    } catch (requestError) {
      setError(requestError.message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="app-shell">
      <section className="card">
        <h1>Order Demo</h1>
        <p>Click the button to place an order through the microservice flow.</p>
        <button onClick={placeOrder} disabled={loading}>
          {loading ? "Placing Order..." : "Place Order"}
        </button>

        {error && <p className="error">{error}</p>}

        {result && (
          <pre>{JSON.stringify(result, null, 2)}</pre>
        )}
      </section>
    </main>
  );
}