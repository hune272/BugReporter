import { createBrowserRouter, Navigate } from 'react-router-dom';
import AppLayout from '@shared/components/layout/AppLayout.jsx';
import ProtectedRoute from '@shared/components/layout/ProtectedRoute.jsx';
import LoginPage from '@features/auth/pages/LoginPage.jsx';
import BugListPage from '@features/bugs/pages/BugListPage.jsx';
import BugReportPage from '@features/bugs/pages/BugReportPage.jsx';
import { USER_ROLES } from '@shared/utils/constants.js';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },

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
      // TODO: { path: '/bugs/:id', element: <BugDetailPage /> },
    ],
  },

  // Moderator-only routes
  {
    element: (
      <ProtectedRoute requireRole={USER_ROLES.MODERATOR}>
        <AppLayout />
      </ProtectedRoute>
    ),
    children: [
      // TODO: { path: '/admin/users', element: <AdminUsersPage /> },
    ],
  },

  { path: '*', element: <Navigate to="/bugs" replace /> },
]);
