import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/company.dart';
import '../models/bill.dart';
import '../services/api_service.dart';
import 'bill_detail_screen.dart';

class BillsScreen extends StatefulWidget {
  final Company company;
  final String initialType;
  final String initialStatus;

  const BillsScreen({
    super.key,
    required this.company,
    this.initialType   = 'ALL',
    this.initialStatus = 'OPENED',
  });

  @override
  State<BillsScreen> createState() => _BillsScreenState();
}

class _BillsScreenState extends State<BillsScreen> {
  final _searchCtrl = TextEditingController();
  List<Bill> _bills = [];
  int _total        = 0;
  int _page         = 0;
  bool _loading     = false;
  late String _type;
  late String _status;
  final _fmt = NumberFormat('#,##,##0.00', 'en_IN');

  @override
  void initState() {
    super.initState();
    _type   = widget.initialType;
    _status = widget.initialStatus;
    _load();
  }

  @override
  void dispose() {
    _searchCtrl.dispose();
    super.dispose();
  }

  Future<void> _load({bool reset = true}) async {
    if (reset) { _page = 0; _bills = []; }
    setState(() => _loading = true);
    try {
      final data = await ApiService.getBills(
        companyId: widget.company.id,
        type:      _type,
        status:    _status,
        search:    _searchCtrl.text,
        page:      _page,
        size:      20,
      );
      setState(() {
        if (reset) {
          _bills = data['bills'] as List<Bill>;
        } else {
          _bills.addAll(data['bills'] as List<Bill>);
        }
        _total = data['total'] as int;
      });
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text('Error: $e')));
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF1A1A2E),
      appBar: AppBar(
        backgroundColor: const Color(0xFF16213E),
        title: Text(widget.company.name,
            style: const TextStyle(color: Color(0xFFD4AF37), fontSize: 16)),
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(110),
          child: Column(children: [
            // Search bar
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
              child: TextField(
                controller: _searchCtrl,
                style: const TextStyle(color: Colors.white),
                decoration: InputDecoration(
                  hintText: 'Search bill number or customer...',
                  hintStyle: const TextStyle(color: Colors.white38),
                  prefixIcon: const Icon(Icons.search, color: Colors.white38),
                  suffixIcon: _searchCtrl.text.isNotEmpty
                      ? IconButton(icon: const Icon(Icons.clear, color: Colors.white38),
                          onPressed: () { _searchCtrl.clear(); _load(); })
                      : null,
                  filled: true, fillColor: const Color(0xFF1A1A2E),
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(8),
                      borderSide: BorderSide.none),
                ),
                onSubmitted: (_) => _load(),
                onChanged: (v) { if (v.isEmpty) _load(); },
              ),
            ),
            // Filters
            SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
              child: Row(children: [
                _filterChip('ALL',    _type == 'ALL'),
                _filterChip('GOLD',   _type == 'GOLD'),
                _filterChip('SILVER', _type == 'SILVER'),
                const SizedBox(width: 12),
                _statusChip('OPENED',   _status == 'OPENED'),
                _statusChip('CLOSED',   _status == 'CLOSED'),
                _statusChip('ALL',      _status == 'ALL'),
              ]),
            ),
          ]),
        ),
      ),
      body: Column(children: [
        // Summary bar
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          color: const Color(0xFF16213E),
          child: Row(children: [
            Text('$_total bills found',
                style: const TextStyle(color: Colors.white70, fontSize: 13)),
          ]),
        ),
        // Bill list
        Expanded(
          child: _loading && _bills.isEmpty
              ? const Center(child: CircularProgressIndicator(color: Color(0xFFD4AF37)))
              : _bills.isEmpty
                  ? const Center(child: Text('No bills found',
                      style: TextStyle(color: Colors.white54)))
                  : ListView.builder(
                      padding: const EdgeInsets.all(12),
                      itemCount: _bills.length + (_bills.length < _total ? 1 : 0),
                      itemBuilder: (ctx, i) {
                        if (i == _bills.length) {
                          return Padding(
                            padding: const EdgeInsets.all(16),
                            child: ElevatedButton(
                              onPressed: () { _page++; _load(reset: false); },
                              style: ElevatedButton.styleFrom(
                                  backgroundColor: const Color(0xFFD4AF37)),
                              child: const Text('Load More', style: TextStyle(color: Colors.black)),
                            ),
                          );
                        }
                        return _buildBillCard(_bills[i]);
                      },
                    ),
        ),
      ]),
    );
  }

  Widget _buildBillCard(Bill bill) {
    final goldColor = bill.isGold ? Colors.yellow.shade700 : Colors.grey.shade400;
    final statusColor = bill.status == 'OPENED' ? Colors.green :
                        bill.status == 'CLOSED'  ? Colors.orange : Colors.blue;

    return Card(
      color: const Color(0xFF16213E),
      margin: const EdgeInsets.only(bottom: 10),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) =>
            BillDetailScreen(
              companyId:  widget.company.id,
              billNumber: bill.billNumber,
              type:       bill.materialType,
              companyName: widget.company.name,
            ))),
        child: Padding(
          padding: const EdgeInsets.all(14),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Row(children: [
              Icon(bill.isGold ? Icons.star : Icons.circle, color: goldColor, size: 18),
              const SizedBox(width: 6),
              Text(bill.billNumber, style: const TextStyle(color: Colors.white,
                  fontWeight: FontWeight.bold, fontSize: 15)),
              const Spacer(),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                decoration: BoxDecoration(
                  color: statusColor.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(6),
                  border: Border.all(color: statusColor.withOpacity(0.5)),
                ),
                child: Text(bill.status, style: TextStyle(color: statusColor, fontSize: 11)),
              ),
            ]),
            const SizedBox(height: 8),
            Text(bill.customerName, style: const TextStyle(color: Colors.white70, fontSize: 14)),
            if (bill.mobileNumber != null)
              Text(bill.mobileNumber!, style: const TextStyle(color: Colors.white38, fontSize: 12)),
            const SizedBox(height: 8),
            Row(children: [
              _infoTag('₹ ${_fmt.format(bill.amount)}', Colors.blue),
              const SizedBox(width: 8),
              if (bill.openingDate != null) _infoTag(bill.openingDate!, Colors.white38),
            ]),
          ]),
        ),
      ),
    );
  }

  Widget _filterChip(String label, bool selected) => Padding(
    padding: const EdgeInsets.only(right: 8),
    child: InkWell(
      onTap: () { setState(() => _type = label); _load(); },
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
        decoration: BoxDecoration(
          color: selected ? const Color(0xFFD4AF37) : const Color(0xFF1A1A2E),
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: const Color(0xFFD4AF37).withOpacity(0.5)),
        ),
        child: Text(label, style: TextStyle(
            color: selected ? Colors.black : Colors.white70, fontSize: 12)),
      ),
    ),
  );

  Widget _statusChip(String label, bool selected) => Padding(
    padding: const EdgeInsets.only(right: 8),
    child: InkWell(
      onTap: () { setState(() => _status = label); _load(); },
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
        decoration: BoxDecoration(
          color: selected ? Colors.green : const Color(0xFF1A1A2E),
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: Colors.green.withOpacity(0.5)),
        ),
        child: Text(label, style: TextStyle(
            color: selected ? Colors.black : Colors.white70, fontSize: 12)),
      ),
    ),
  );

  Widget _infoTag(String text, Color color) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
    decoration: BoxDecoration(
      color: color.withOpacity(0.1),
      borderRadius: BorderRadius.circular(4),
    ),
    child: Text(text, style: TextStyle(color: color, fontSize: 12)),
  );
}
