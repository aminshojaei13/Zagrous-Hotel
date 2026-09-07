import './styles/main.css'
import { LoginForm } from './components/reservation/LoginForm'
import { ReservationPage } from './components/reservation/ReservationPage'
import { useReservationViewModel } from './hooks/useReservationViewModel'

function App() {
  const { state } = useReservationViewModel();

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
