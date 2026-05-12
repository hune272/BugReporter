import { createBrowserRouter, Navigate } from 'react-router-dom';
import AppLayout from '@shared/components/layout/AppLayout.jsx';
import ProtectedRoute from '@app/ProtectedRoute.jsx';
import LoginPage from '@features/auth/pages/LoginPage.jsx';
import RegisterPage from '@features/auth/pages/RegisterPage.jsx';
import BugListPage from '@features/bugs/pages/BugListPage.jsx';
import BugReportPage from '@features/bugs/pages/BugReportPage.jsx';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },

  {
    element: (
        <ProtectedRoute>
          <AppLayout />
        </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Navigate to="/bugs" replace /> },
      { path: '/bugs', element: <BugListPage /> },
      { path: '/bugs/new', element: <BugReportPage /> },
      { path: '/bugs/:id/edit', element: <BugReportPage /> },
    ],
  },

  { path: '*', element: <Navigate to="/bugs" replace /> },
]);
