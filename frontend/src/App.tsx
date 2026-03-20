import React from 'react';
import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom';
import { AcordoList } from './components/AcordoList';
import { AcordoDetail } from './components/AcordoDetail';
import { ProdutoVerbaList } from './components/ProdutoVerbaList';
import { AcordoPeriodoList } from './components/AcordoPeriodoList';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <div className="app-shell">
        <header className="app-header">
          <span className="app-title">Monitor Acordo Comercial</span>
          <nav className="app-nav">
            <NavLink to="/" end className={({ isActive }) => 'nav-tab' + (isActive ? ' nav-tab-active' : '')}>
              Acordos
            </NavLink>
            <NavLink to="/produtos" className={({ isActive }) => 'nav-tab' + (isActive ? ' nav-tab-active' : '')}>
              Produtos
            </NavLink>
          </nav>
        </header>
        <main className="app-main">
          <Routes>
            <Route path="/" element={<AcordoList />} />
            <Route path="/acordos/:id" element={<AcordoDetail />} />
            <Route path="/produtos" element={<ProdutoVerbaList />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
};

export default App;
