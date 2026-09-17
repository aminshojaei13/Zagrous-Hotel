import './styles/main.css'
import { LoginForm } from './components/reservation/LoginForm'
import { ReservationPage } from './components/reservation/ReservationPage'
import { AdminPage } from './features/admin/AdminPage'
import { useReservationViewModel } from './hooks/useReservationViewModel'

function App() {
  const { state } = useReservationViewModel();

  const urlParams = new URLSearchParams(window.location.search);
  const isAdmin = window.location.port === "8091" || urlParams.get('mode') === 'admin';

  if (isAdmin) {
    return <AdminPage />;
  }

  return (
    <div className="container">
      {!state.isLoggedIn ? (
        <LoginForm />
      ) : (
        <ReservationPage />
      )}
    </div>
  )
}

export default App
