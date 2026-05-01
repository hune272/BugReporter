import { Outlet } from 'react-router-dom';
import { useAuth } from '../../../features/auth/hooks/useAuth.js';
import Sidebar from './Sidebar.jsx';
import './AppLayout.css';

function AppLayout({ children }) {
    const { user } = useAuth();

    return (
        <div className="app-layout">
            <Sidebar user={user} />

            <main className="app-layout__content">
                {children ?? <Outlet />}
            </main>
        </div>
    );
}

export default AppLayout;
