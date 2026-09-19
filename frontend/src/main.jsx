import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  Activity,
  AlertTriangle,
  ArrowRight,
  BarChart3,
  Bell,
  Clock3,
  DollarSign,
  Gauge,
  Lock,
  Mail,
  Search,
  Server,
  Settings,
  ShieldCheck,
  Sparkles,
  User,
  Zap
} from 'lucide-react';
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';
import './styles.css';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const fallbackDashboard = {
  summary: { totalRequests: 1284932, totalCost: 428.32, errorRate: 2.41, averageLatencyMs: 183 },
  costByProvider: [
    { label: 'OpenAI', requests: 728410, cost: 241.28 },
    { label: 'Anthropic', requests: 316220, cost: 103.76 },
    { label: 'Gemini', requests: 181984, cost: 60.92 },
    { label: 'Stripe', requests: 58318, cost: 22.36 }
  ],
  topEndpoints: [
    { label: '/v1/chat/completions', requests: 728410, cost: 241.28 },
    { label: '/v1/messages', requests: 316220, cost: 103.76 },
    { label: '/generateContent', requests: 181984, cost: 60.92 },
    { label: '/v1/payment_intents', requests: 58318, cost: 22.36 }
  ],
  dailyTrend: Array.from({ length: 14 }, (_, index) => ({
    day: `Sep ${index + 1}`,
    requests: 32000 + index * 1700 + (index % 3) * 2800,
    cost: 10 + index * 1.7 + (index % 4) * 2.2,
    errorRate: 1.4 + (index % 5) * 0.4,
    averageLatencyMs: 155 + (index % 6) * 12
  })),
  alerts: [
    { id: 1, type: 'ANOMALY', threshold: 150, currentValue: 188, status: 'TRIGGERED', createdAt: new Date().toISOString() },
    { id: 2, type: 'ERROR_RATE', threshold: 5, currentValue: 2.41, status: 'ACTIVE', createdAt: new Date().toISOString() }
  ]
};

const fallbackProviders = [
  { id: 1, name: 'OpenAI', type: 'AI', status: 'ACTIVE' },
  { id: 2, name: 'Anthropic', type: 'AI', status: 'ACTIVE' },
  { id: 3, name: 'Gemini', type: 'AI', status: 'ACTIVE' },
  { id: 4, name: 'Stripe', type: 'Payments', status: 'ACTIVE' }
];

const fallbackPricing = [
  { id: 1, provider: 'OpenAI', model: 'gpt-5', inputPricePerUnit: 0.00001, outputPricePerUnit: 0.00003, currency: 'USD' },
  { id: 2, provider: 'Anthropic', model: 'claude-4', inputPricePerUnit: 0.000008, outputPricePerUnit: 0.000024, currency: 'USD' },
  { id: 3, provider: 'Gemini', model: 'gemini-pro', inputPricePerUnit: 0.000004, outputPricePerUnit: 0.000012, currency: 'USD' }
];

const fallbackUsage = [
  { provider: 'OpenAI', endpoint: '/v1/chat/completions', statusCode: 200, latencyMs: 184, cost: 0.08321, timestamp: new Date().toISOString() },
  { provider: 'Anthropic', endpoint: '/v1/messages', statusCode: 200, latencyMs: 231, cost: 0.06442, timestamp: new Date().toISOString() },
  { provider: 'Gemini', endpoint: '/generateContent', statusCode: 500, latencyMs: 711, cost: 0.02181, timestamp: new Date().toISOString() },
  { provider: 'Stripe', endpoint: '/v1/payment_intents', statusCode: 200, latencyMs: 91, cost: 0, timestamp: new Date().toISOString() }
];

function formatCurrency(value) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 2 }).format(Number(value || 0));
}

function compactNumber(value) {
  return new Intl.NumberFormat('en-US', { notation: 'compact', maximumFractionDigits: 1 }).format(Number(value || 0));
}

async function fetchJson(path, fallback) {
  try {
    const response = await fetch(`${API_BASE}${path}`);
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return { data: await response.json(), live: true };
  } catch {
    return { data: fallback, live: false };
  }
}

function App() {
  const [route, setRoute] = useState('landing');
  const [authMode, setAuthMode] = useState('login');
  const [activeView, setActiveView] = useState('Dashboard');
  const [dashboard, setDashboard] = useState(fallbackDashboard);
  const [providers, setProviders] = useState(fallbackProviders);
  const [pricing, setPricing] = useState(fallbackPricing);
  const [usage, setUsage] = useState(fallbackUsage);
  const [isLive, setIsLive] = useState(false);
  const [query, setQuery] = useState('');

  useEffect(() => {
    Promise.all([
      fetchJson('/dashboard', fallbackDashboard),
      fetchJson('/providers', fallbackProviders),
      fetchJson('/pricing', fallbackPricing),
      fetchJson('/usage?limit=60', fallbackUsage)
    ]).then(([dashboardResponse, providerResponse, pricingResponse, usageResponse]) => {
      setDashboard(dashboardResponse.data);
      setProviders(providerResponse.data);
      setPricing(pricingResponse.data);
      setUsage(usageResponse.data);
      setIsLive(dashboardResponse.live);
    });
  }, []);

  const filteredUsage = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    if (!normalized) return usage;
    return usage.filter((row) => `${row.provider} ${row.endpoint} ${row.statusCode}`.toLowerCase().includes(normalized));
  }, [query, usage]);

  const openAuth = (mode) => {
    setAuthMode(mode);
    setRoute('auth');
  };

  if (route === 'landing') {
    return <LandingPage dashboard={dashboard} openAuth={openAuth} openDashboard={() => setRoute('dashboard')} />;
  }

  if (route === 'auth') {
    return (
      <AuthPage
        mode={authMode}
        setMode={setAuthMode}
        onSubmit={() => setRoute('dashboard')}
        goHome={() => setRoute('landing')}
      />
    );
  }

  return (
    <DashboardShell
      activeView={activeView}
      setActiveView={setActiveView}
      dashboard={dashboard}
      providers={providers}
      pricing={pricing}
      filteredUsage={filteredUsage}
      query={query}
      setQuery={setQuery}
      isLive={isLive}
      signOut={() => setRoute('landing')}
    />
  );
}

function LandingPage({ dashboard, openAuth, openDashboard }) {
  const summary = dashboard.summary || fallbackDashboard.summary;
  return (
    <main className="landing-page">
      <header className="landing-nav">
        <Brand liveLabel="Neon FinOps" />
        <div className="landing-actions">
          <button className="ghost-button" onClick={() => openAuth('login')}>Login</button>
          <button className="primary-button" onClick={() => openAuth('register')}>Register</button>
        </div>
      </header>

      <section className="hero">
        <div className="hero-copy">
          <span className="eyebrow"><Sparkles size={16} /> Real-time API cost command center</span>
          <h1>API Cost Analytics</h1>
          <p>
            Track provider spend, request volume, latency, errors, pricing changes, and alert rules
            before usage spikes become billing surprises.
          </p>
          <div className="hero-actions">
            <button className="primary-button large" onClick={() => openAuth('register')}>
              Start monitoring <ArrowRight size={18} />
            </button>
            <button className="ghost-button large" onClick={openDashboard}>View demo dashboard</button>
          </div>
        </div>

        <div className="hero-dashboard" aria-label="Dashboard preview">
          <div className="preview-top">
            <span>Live spend</span>
            <strong>{formatCurrency(summary.totalCost)}</strong>
          </div>
          <ResponsiveContainer width="100%" height={190}>
            <AreaChart data={dashboard.dailyTrend}>
              <defs>
                <linearGradient id="pinkGlow" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#ff2fb3" stopOpacity={0.75} />
                  <stop offset="95%" stopColor="#ff2fb3" stopOpacity={0.04} />
                </linearGradient>
              </defs>
              <XAxis dataKey="day" hide />
              <YAxis hide />
              <Tooltip contentStyle={tooltipStyle} />
              <Area type="monotone" dataKey="cost" stroke="#ff2fb3" strokeWidth={3} fill="url(#pinkGlow)" />
            </AreaChart>
          </ResponsiveContainer>
          <div className="preview-metrics">
            <Metric icon={Activity} label="Requests" value={compactNumber(summary.totalRequests)} />
            <Metric icon={AlertTriangle} label="Error rate" value={`${Number(summary.errorRate || 0).toFixed(2)}%`} />
          </div>
        </div>
      </section>
    </main>
  );
}

function AuthPage({ mode, setMode, onSubmit, goHome }) {
  const isRegister = mode === 'register';
  return (
    <main className="auth-page">
      <button className="home-link" onClick={goHome}><Zap size={18} /> API Cost Analytics</button>
      <section className="auth-panel">
        <div className="auth-copy">
          <span className="eyebrow"><ShieldCheck size={16} /> Secure workspace access</span>
          <h1>{isRegister ? 'Create your account' : 'Welcome back'}</h1>
          <p>{isRegister ? 'Register your team workspace and start tracking provider costs.' : 'Login to review usage, alerts, pricing, and dashboard health.'}</p>
        </div>
        <form className="auth-form" onSubmit={(event) => { event.preventDefault(); onSubmit(); }}>
          <div className="mode-switch">
            <button type="button" className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>Login</button>
            <button type="button" className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>Register</button>
          </div>
          {isRegister && <Field icon={User} label="Full name" type="text" placeholder="Riya Sharma" />}
          {isRegister && <Field icon={Server} label="Organization" type="text" placeholder="Acme API Platform" />}
          <Field icon={Mail} label="Email" type="email" placeholder="riya@example.com" />
          <Field icon={Lock} label="Password" type="password" placeholder="Enter password" />
          <button className="primary-button auth-submit" type="submit">
            {isRegister ? 'Create account' : 'Login'} <ArrowRight size={18} />
          </button>
        </form>
      </section>
    </main>
  );
}

function Field({ icon: Icon, label, ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <div>
        <Icon size={17} />
        <input {...props} />
      </div>
    </label>
  );
}

function DashboardShell({ activeView, setActiveView, dashboard, providers, pricing, filteredUsage, query, setQuery, isLive, signOut }) {
  const navItems = [
    ['Dashboard', BarChart3],
    ['Providers', Server],
    ['Usage', Activity],
    ['Analytics', Gauge],
    ['Pricing', DollarSign],
    ['Alerts', Bell]
  ];

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <Brand liveLabel={isLive ? 'Live Spring API' : 'Demo dataset'} />
        <nav>
          {navItems.map(([label, Icon]) => (
            <button
              key={label}
              className={activeView === label ? 'nav-item active' : 'nav-item'}
              onClick={() => setActiveView(label)}
              title={label}
            >
              <Icon size={18} />
              <span>{label}</span>
            </button>
          ))}
        </nav>
        <div className="sidebar-status">
          <ShieldCheck size={18} />
          <span>RBAC ready structure</span>
        </div>
      </aside>

      <main className="main">
        <header className="topbar">
          <div>
            <h1>{activeView}</h1>
            <p>Provider usage, latency, errors, and spend in one operational view.</p>
          </div>
          <div className="toolbar">
            <button className="icon-button" title="Settings"><Settings size={18} /></button>
            <span className={isLive ? 'status-pill live' : 'status-pill'}>{isLive ? 'API connected' : 'Fallback data'}</span>
            <button className="ghost-button compact" onClick={signOut}>Sign out</button>
          </div>
        </header>

        {activeView === 'Dashboard' && <Dashboard dashboard={dashboard} />}
        {activeView === 'Providers' && <Providers providers={providers} />}
        {activeView === 'Usage' && <Usage rows={filteredUsage} query={query} setQuery={setQuery} />}
        {activeView === 'Analytics' && <Analytics dashboard={dashboard} />}
        {activeView === 'Pricing' && <Pricing pricing={pricing} />}
        {activeView === 'Alerts' && <Alerts alerts={dashboard.alerts || []} />}
      </main>
    </div>
  );
}

function Brand({ liveLabel }) {
  return (
    <div className="brand">
      <div className="brand-mark"><Zap size={20} /></div>
      <div>
        <strong>API Cost Analytics</strong>
        <span>{liveLabel}</span>
      </div>
    </div>
  );
}

function Dashboard({ dashboard }) {
  const summary = dashboard.summary || fallbackDashboard.summary;
  return (
    <section className="view-stack">
      <div className="metrics-grid">
        <Metric icon={Activity} label="Requests" value={compactNumber(summary.totalRequests)} />
        <Metric icon={DollarSign} label="Cost" value={formatCurrency(summary.totalCost)} />
        <Metric icon={AlertTriangle} label="Errors" value={`${Number(summary.errorRate || 0).toFixed(2)}%`} />
        <Metric icon={Clock3} label="Latency" value={`${Math.round(summary.averageLatencyMs || 0)}ms`} />
      </div>
      <div className="dashboard-grid">
        <Panel title="Cost by provider">
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={dashboard.costByProvider}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#2a2030" />
              <XAxis dataKey="label" stroke="#b48aa7" />
              <YAxis tickFormatter={(value) => `$${value}`} stroke="#b48aa7" />
              <Tooltip contentStyle={tooltipStyle} formatter={(value) => formatCurrency(value)} />
              <Bar dataKey="cost" fill="#ff2fb3" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </Panel>
        <Panel title="Requests and spend">
          <ResponsiveContainer width="100%" height={260}>
            <AreaChart data={dashboard.dailyTrend}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#2a2030" />
              <XAxis dataKey="day" tickFormatter={(value) => String(value).slice(5)} stroke="#b48aa7" />
              <YAxis stroke="#b48aa7" />
              <Tooltip contentStyle={tooltipStyle} />
              <Area dataKey="requests" stroke="#ff2fb3" fill="#3a0f2d" />
            </AreaChart>
          </ResponsiveContainer>
        </Panel>
      </div>
      <div className="dashboard-grid">
        <Breakdown title="Top endpoints" rows={dashboard.topEndpoints} />
        <Alerts alerts={dashboard.alerts || []} compact />
      </div>
    </section>
  );
}

function Metric({ icon: Icon, label, value }) {
  return (
    <div className="metric">
      <Icon size={20} />
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function Panel({ title, children }) {
  return (
    <section className="panel">
      <h2>{title}</h2>
      {children}
    </section>
  );
}

function Breakdown({ title, rows = [] }) {
  const max = Math.max(...rows.map((row) => Number(row.cost || 0)), 1);
  return (
    <Panel title={title}>
      <div className="breakdown-list">
        {rows.map((row) => (
          <div className="breakdown-row" key={row.label}>
            <span>{row.label}</span>
            <div className="bar-track">
              <div className="bar-fill" style={{ width: `${(Number(row.cost || 0) / max) * 100}%` }} />
            </div>
            <strong>{formatCurrency(row.cost)}</strong>
          </div>
        ))}
      </div>
    </Panel>
  );
}

function Providers({ providers }) {
  return (
    <section className="table-panel">
      <Table
        columns={['Provider', 'Type', 'Status']}
        rows={providers.map((provider) => [provider.name, provider.type, <Badge key={provider.id}>{provider.status}</Badge>])}
      />
    </section>
  );
}

function Usage({ rows, query, setQuery }) {
  return (
    <section className="view-stack">
      <div className="search-row">
        <Search size={18} />
        <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search provider, endpoint, status" />
      </div>
      <section className="table-panel">
        <Table
          columns={['Time', 'Provider', 'Endpoint', 'Status', 'Latency', 'Cost']}
          rows={rows.map((row) => [
            new Date(row.timestamp).toLocaleString(),
            row.provider,
            row.endpoint,
            <Badge key={`${row.timestamp}-${row.endpoint}`} tone={row.statusCode >= 400 ? 'danger' : 'ok'}>{row.statusCode}</Badge>,
            `${row.latencyMs}ms`,
            formatCurrency(row.cost)
          ])}
        />
      </section>
    </section>
  );
}

function Analytics({ dashboard }) {
  return (
    <section className="dashboard-grid">
      <Panel title="Error rate">
        <ResponsiveContainer width="100%" height={260}>
          <LineChart data={dashboard.dailyTrend}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#2a2030" />
            <XAxis dataKey="day" tickFormatter={(value) => String(value).slice(5)} stroke="#b48aa7" />
            <YAxis stroke="#b48aa7" />
            <Tooltip contentStyle={tooltipStyle} />
            <Line type="monotone" dataKey="errorRate" stroke="#ff4a6e" strokeWidth={2} dot={false} />
          </LineChart>
        </ResponsiveContainer>
      </Panel>
      <Panel title="Latency trend">
        <ResponsiveContainer width="100%" height={260}>
          <LineChart data={dashboard.dailyTrend}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#2a2030" />
            <XAxis dataKey="day" tickFormatter={(value) => String(value).slice(5)} stroke="#b48aa7" />
            <YAxis stroke="#b48aa7" />
            <Tooltip contentStyle={tooltipStyle} />
            <Line type="monotone" dataKey="averageLatencyMs" stroke="#ff2fb3" strokeWidth={2} dot={false} />
          </LineChart>
        </ResponsiveContainer>
      </Panel>
      <Breakdown title="Provider cost allocation" rows={dashboard.costByProvider} />
      <Breakdown title="Endpoint cost allocation" rows={dashboard.topEndpoints} />
    </section>
  );
}

function Pricing({ pricing }) {
  return (
    <section className="table-panel">
      <Table
        columns={['Provider', 'Model', 'Input/unit', 'Output/unit', 'Currency']}
        rows={pricing.map((plan) => [
          plan.provider,
          plan.model,
          Number(plan.inputPricePerUnit).toFixed(8),
          Number(plan.outputPricePerUnit).toFixed(8),
          plan.currency
        ])}
      />
    </section>
  );
}

function Alerts({ alerts, compact = false }) {
  return (
    <Panel title={compact ? 'Active alerts' : 'Alert rules'}>
      <div className="alert-list">
        {alerts.map((alert) => (
          <div className="alert-row" key={alert.id}>
            <div>
              <strong>{alert.type.replace('_', ' ')}</strong>
              <span>Threshold {alert.threshold} / current {alert.currentValue}</span>
            </div>
            <Badge tone={alert.status === 'TRIGGERED' ? 'danger' : 'ok'}>{alert.status}</Badge>
          </div>
        ))}
      </div>
    </Panel>
  );
}

function Table({ columns, rows }) {
  return (
    <table>
      <thead>
        <tr>{columns.map((column) => <th key={column}>{column}</th>)}</tr>
      </thead>
      <tbody>
        {rows.map((row, index) => (
          <tr key={index}>{row.map((cell, cellIndex) => <td key={cellIndex}>{cell}</td>)}</tr>
        ))}
      </tbody>
    </table>
  );
}

function Badge({ children, tone = 'neutral' }) {
  return <span className={`badge ${tone}`}>{children}</span>;
}

const tooltipStyle = {
  background: '#120914',
  border: '1px solid #ff2fb3',
  borderRadius: 8,
  color: '#fff4fb'
};

createRoot(document.getElementById('root')).render(<App />);
