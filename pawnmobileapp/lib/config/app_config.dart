class AppConfig {
  // ── Update this to your Railway service URL after deployment ──
  // Format: https://<your-service>.up.railway.app
  static const String baseUrl = 'https://pawnwebservice-production.up.railway.app';

  static const String loginEndpoint      = '$baseUrl/api/auth/login';
  static const String companiesEndpoint  = '$baseUrl/api/companies';
  static const String billsEndpoint      = '$baseUrl/api/bills';
  static const String dashboardEndpoint  = '$baseUrl/api/dashboard';
  static const String customersEndpoint  = '$baseUrl/api/customers';
}
