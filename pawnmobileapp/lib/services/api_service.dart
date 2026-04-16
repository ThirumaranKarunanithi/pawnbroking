import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../config/app_config.dart';
import '../models/user.dart';
import '../models/company.dart';
import '../models/bill.dart';

class ApiService {
  static User? _currentUser;
  static User? get currentUser => _currentUser;

  // ── Auth ──────────────────────────────────────────────────────────────────

  static Future<User> login(String username, String password) async {
    final res = await http.post(
      Uri.parse(AppConfig.loginEndpoint),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'username': username, 'password': password}),
    );

    if (res.statusCode == 200) {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      _currentUser = User.fromJson(data);
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('token', _currentUser!.token);
      await prefs.setString('userName', _currentUser!.userName);
      await prefs.setString('employeeName', _currentUser!.employeeName);
      return _currentUser!;
    } else {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      throw Exception(data['error'] ?? 'Login failed');
    }
  }

  static Future<void> logout() async {
    _currentUser = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
  }

  static Future<bool> isLoggedIn() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('token') != null;
  }

  // ── Companies ─────────────────────────────────────────────────────────────

  static Future<List<Company>> getCompanies() async {
    final res = await http.get(Uri.parse(AppConfig.companiesEndpoint));
    _checkStatus(res);
    final list = jsonDecode(res.body) as List;
    return list.map((e) => Company.fromJson(e as Map<String, dynamic>)).toList();
  }

  // ── Bills ─────────────────────────────────────────────────────────────────

  static Future<Map<String, dynamic>> getBills({
    required int companyId,
    String type = 'ALL',
    String status = 'OPENED',
    String search = '',
    int page = 0,
    int size = 20,
  }) async {
    final uri = Uri.parse(AppConfig.billsEndpoint).replace(queryParameters: {
      'companyId': companyId.toString(),
      'type':      type,
      'status':    status,
      'search':    search,
      'page':      page.toString(),
      'size':      size.toString(),
    });
    final res = await http.get(uri);
    _checkStatus(res);
    final data = jsonDecode(res.body) as Map<String, dynamic>;
    final billList = (data['bills'] as List)
        .map((e) => Bill.fromJson(e as Map<String, dynamic>))
        .toList();
    return {'bills': billList, 'total': data['total']};
  }

  static Future<Map<String, dynamic>> getBillDetail({
    required int companyId,
    required String billNumber,
    required String type,
  }) async {
    final uri = Uri.parse('${AppConfig.billsEndpoint}/$billNumber')
        .replace(queryParameters: {
      'companyId': companyId.toString(),
      'type':      type,
    });
    final res = await http.get(uri);
    _checkStatus(res);
    return jsonDecode(res.body) as Map<String, dynamic>;
  }

  // ── Dashboard ─────────────────────────────────────────────────────────────

  static Future<Map<String, dynamic>> getDashboard({
    required int companyId,
    String? date,
  }) async {
    final params = {'companyId': companyId.toString()};
    if (date != null) params['date'] = date;
    final uri = Uri.parse(AppConfig.dashboardEndpoint).replace(queryParameters: params);
    final res = await http.get(uri);
    _checkStatus(res);
    return jsonDecode(res.body) as Map<String, dynamic>;
  }

  // ── Customers ─────────────────────────────────────────────────────────────

  static Future<List<Map<String, dynamic>>> searchCustomers(String query) async {
    final uri = Uri.parse('${AppConfig.customersEndpoint}/search')
        .replace(queryParameters: {'query': query});
    final res = await http.get(uri);
    _checkStatus(res);
    return (jsonDecode(res.body) as List).cast<Map<String, dynamic>>();
  }

  static void _checkStatus(http.Response res) {
    if (res.statusCode < 200 || res.statusCode >= 300) {
      throw Exception('API error ${res.statusCode}: ${res.body}');
    }
  }
}
