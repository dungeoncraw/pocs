import { createRoot } from "react-dom/client";
import * as React from "react";
import { useMemoOne } from "use-memo-one";
import "./index.css";
import { memo, useEffect, useState } from "react";

const computeValue = (base) => {
  const start = performance.now();
  while (performance.now() - start < 50) {}
  return {
    result: base * 2,
    timestamp: Date.now()
  };
};

const Child = memo(({ data, onUpdate }) => {
  const [highlight, setHighlight] = useState(false);

  useEffect(() => {
    setHighlight(true);
    const timer = setTimeout(() => setHighlight(false), 1000);
    return () => clearTimeout(timer);
  }, [data]);

  return (
    <div className={`child-component ${highlight ? 'highlight' : ''}`}>
      <h3>Memoized Child Component</h3>
      <p><strong>Computed value:</strong> {data.result}</p>
      <p><strong>Computed at:</strong> {new Date(data.timestamp).toLocaleTimeString()}</p>
      <button onClick={onUpdate}>Update Parent State (No Re-render)</button>
    </div>
  );
});

const Parent = () => {
  const [count, setCount] = useState(1);
  const [parentState, setParentState] = useState(0);
  const [lastRender, setLastRender] = useState(Date.now());

  useEffect(() => {
    setLastRender(Date.now());
  });

  const memoizedData = useMemoOne(
    () => computeValue(count),
    [count]
  );

  const updateParent = useMemoOne(
    () => () => setParentState(s => s + 1),
    []
  );

  return (
    <div style={{ fontFamily: 'system-ui', maxWidth: '600px', margin: '20px auto' }}>
      <div className="info-box">
        <p><strong>Parent Render Count:</strong> {parentState}</p>
        <p><strong>Last Parent Render:</strong> {new Date(lastRender).toLocaleTimeString()}</p>
        <p><strong>Current Count:</strong> {count}</p>
        <button onClick={() => setCount(c => c + 1)}>
          Increment Count (Triggers Re-render)
        </button>
      </div>

      <div style={{ marginTop: '20px' }}>
        <Child
          data={memoizedData}
          onUpdate={updateParent}
        />
      </div>
    </div>
  );
};

const root = document.getElementById("root");
createRoot(root).render(<Parent />);
