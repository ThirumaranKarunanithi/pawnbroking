import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../services/api_service.dart';
import '../models/company.dart';
import 'login_screen.dart';
import 'bills_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  List<Company> _companies = [];
  Company? _selected;
  Map<String, dynamic> _dashboard = {};
  bool _loadingCompanies = true;
  bool _loadingDashboard = false;
  final _fmt = NumberFormat('#,##,##0.00', 'en_IN');
  final _today = DateFormat('yyyy-MM-dd').format(DateTime.now());

  @override
  void initState() {
    super.initState();
    _loadCompanies();
  }

  Future<void> _loadCompanies() async {
    try {
      final list = await ApiService.getCompanies();
      setState(() {
        _companies = list;
        _loadingCompanies = false;
        if (list.isNotEmpty) {
          _selected = list.first;
          _loadDashboard();
        }
      });
    } catch (e) {
      setState(() => _loadingCompanies = false);
    }
  }

  Future<void> _loadDashboard() async {
    if (_selected == null) return;
    setState(() => _loadingDashboard = true);
    try {
      final data = await ApiService.getDashboard(companyId: _selected!.id, date: _today);
      setState(() { _dashboard = data; _loadingDashboard = false; });
    } catch (_) {
      setState(() => _loadingDashboard = false);
    }
  }

  Future<void> _logout() async {
    await ApiService.logout();
    if (!mounted) return;
    Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const LoginScreen()));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF1A1A2E),
      appBar: AppBar(
        backgroundColor: const Color(0xFF16213E),
        title: const Text('Pawnbroking', style: TextStyle(color: Color(0xFFD4AF37))),
        actions: [
          IconButton(icon: const Icon(Icons.refresh, color: Colors.white),
              onPressed: _loadDashboard),
          IconButton(icon: const Icon(Icons.logout, color: Colors.white),
              onPressed: _logout),
        ],
      ),
      body: _loadingCompanies
          ? const Center(child: CircularProgressIndicator(color: Color(0xFFD4AF37)))
          : _companies.isEmpty
              ? const Center(child: Text('No companies found', style: TextStyle(color: Colors.white)))
              : SingleChildScrollView(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _buildCompanySelector(),
                      const SizedBox(height: 20),
                      _buildDateRow(),
                      const SizedBox(height: 16),
                      _loadingDashboard
                          ? const Center(child: CircularProgressIndicator(color: Color(0xFFD4AF37)))
                          : _buildDashboard(),
                      const SizedBox(height: 24),
                      _buildQuickActions(),
                    ],
                  ),
                ),
    );
  }

  Widget _buildCompanySelector() => Container(
    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
    decoration: BoxDecoration(
      color: const Color(0xFF16213E),
      borderRadius: BorderRadius.circular(12),
    ),
    child: DropdownButtonHideUnderline(
      child: DropdownButton<Company>(
        value: _selected,
        dropdownColor: const Color(0xFF16213E),
        isExpanded: true,
        icon: const Icon(Icons.arrow_drop_down, color: Color(0xFFD4AF37)),
        items: _companies.map((c) => DropdownMenuItem(
          value: c,
          child: Text(c.name, style: const TextStyle(color: Colors.white)),
        )).toList(),
        onChanged: (c) { setState(() => _selected = c); _loadDashboard(); },
      ),
    ),
  );

  Widget _buildDateRow() => Row(children: [
    const Icon(Icons.calendar_today, color: Color(0xFFD4AF37), size: 16),
    const SizedBox(width: 8),
    Text(DateFormat('dd MMMM yyyy').format(DateTime.now()),
        style: const TextStyle(color: Colors.white70, fontSize: 14)),
  ]);

  Widget _buildDashboard() {
    final opened     = _dashboard['openedToday'] ?? 0;
    final closed     = _dashboard['closedToday'] ?? 0;
    final totalOpen  = _dashboard['totalOpenBills'] ?? 0;
    final givenToday = _toDouble(_dashboard['given_today']);
    final recvToday  = _toDouble(_dashboard['received_today']);
    final totalLoan  = _toDouble(_dashboard['total_loan_amount']);

    return Column(children: [
      Row(children: [
        _statCard('Opened Today', opened.toString(), Icons.file_open, Colors.green),
        const SizedBox(width: 12),
        _statCard('Closed Today', closed.toString(), Icons.check_circle, Colors.orange),
      ]),
      const SizedBox(height: 12),
      Row(children: [
        _statCard('Active Bills', totalOpen.toString(), Icons.folder_open, const Color(0xFFD4AF37)),
        const SizedBox(width: 12),
        _statCard('Total Loan', '₹${_fmt.format(totalLoan)}', Icons.account_balance, Colors.blue),
      ]),
      const SizedBox(height: 12),
      Row(children: [
        _statCard('Given Today', '₹${_fmt.format(givenToday)}', Icons.arrow_upward, Colors.red),
        const SizedBox(width: 12),
        _statCard('Received Today', '₹${_fmt.format(recvToday)}', Icons.arrow_downward, Colors.green),
      ]),
    ]);
  }

  Widget _statCard(String title, String value, IconData icon, Color color) =>
    Expanded(
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: const Color(0xFF16213E),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Icon(icon, color: color, size: 22),
          const SizedBox(height: 8),
          Text(value, style: TextStyle(color: color, fontSize: 18,
              fontWeight: FontWeight.bold)),
          const SizedBox(height: 4),
          Text(title, style: const TextStyle(color: Colors.white54, fontSize: 12)),
        ]),
      ),
    );

  Widget _buildQuickActions() {
    if (_selected == null) return const SizedBox.shrink();
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      const Text('Quick Actions', style: TextStyle(color: Colors.white,
          fontSize: 16, fontWeight: FontWeight.bold)),
      const SizedBox(height: 12),
      Row(children: [
        _actionBtn('Gold Bills', Icons.star, Colors.yellow.shade700, () =>
            _goToBills('GOLD', 'OPENED')),
        const SizedBox(width: 12),
        _actionBtn('Silver Bills', Icons.circle, Colors.grey, () =>
            _goToBills('SILVER', 'OPENED')),
      ]),
      const SizedBox(height: 12),
      Row(children: [
        _actionBtn('All Active', Icons.folder_open, Colors.green, () =>
            _goToBills('ALL', 'OPENED')),
        const SizedBox(width: 12),
        _actionBtn('Closed Bills', Icons.done_all, Colors.orange, () =>
            _goToBills('ALL', 'CLOSED')),
      ]),
    ]);
  }

  Widget _actionBtn(String label, IconData icon, Color color, VoidCallback onTap) =>
    Expanded(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 16),
          decoration: BoxDecoration(
            color: const Color(0xFF16213E),
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: color.withOpacity(0.4)),
          ),
          child: Column(children: [
            Icon(icon, color: color),
            const SizedBox(height: 6),
            Text(label, style: const TextStyle(color: Colors.white, fontSize: 13)),
          ]),
        ),
      ),
    );

  void _goToBills(String type, String status) {
    if (_selected == null) return;
    Navigator.push(context, MaterialPageRoute(builder: (_) =>
        BillsScreen(company: _selected!, initialType: type, initialStatus: status)));
  }

  double _toDouble(dynamic v) {
    if (v == null) return 0.0;
    if (v is num) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0.0;
  }
}
