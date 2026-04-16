import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../services/api_service.dart';

class BillDetailScreen extends StatefulWidget {
  final int companyId;
  final String billNumber;
  final String type;
  final String companyName;

  const BillDetailScreen({
    super.key,
    required this.companyId,
    required this.billNumber,
    required this.type,
    required this.companyName,
  });

  @override
  State<BillDetailScreen> createState() => _BillDetailScreenState();
}

class _BillDetailScreenState extends State<BillDetailScreen> {
  Map<String, dynamic> _bill = {};
  bool _loading = true;
  final _fmt = NumberFormat('#,##,##0.00', 'en_IN');

  @override
  void initState() {
    super.initState();
    _loadBill();
  }

  Future<void> _loadBill() async {
    try {
      final data = await ApiService.getBillDetail(
        companyId:  widget.companyId,
        billNumber: widget.billNumber,
        type:       widget.type,
      );
      setState(() { _bill = data; _loading = false; });
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
        setState(() => _loading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final status    = _bill['status'] as String? ?? '';
    final isGold    = widget.type == 'GOLD';
    final goldColor = isGold ? Colors.yellow.shade700 : Colors.grey.shade400;

    return Scaffold(
      backgroundColor: const Color(0xFF1A1A2E),
      appBar: AppBar(
        backgroundColor: const Color(0xFF16213E),
        title: Text('Bill: ${widget.billNumber}',
            style: const TextStyle(color: Color(0xFFD4AF37))),
        actions: [
          IconButton(icon: const Icon(Icons.refresh, color: Colors.white),
              onPressed: _loadBill),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator(color: Color(0xFFD4AF37)))
          : _bill.isEmpty
              ? const Center(child: Text('Bill not found', style: TextStyle(color: Colors.white)))
              : SingleChildScrollView(
                  padding: const EdgeInsets.all(16),
                  child: Column(children: [
                    // Header card
                    _headerCard(status, goldColor),
                    const SizedBox(height: 12),
                    _section('Customer Information', [
                      _row('Name',        _bill['customer_name']),
                      _row('Mobile',      _bill['mobile_number']),
                      _row('Mobile 2',    _bill['mobile_number_2']),
                      _row('Address',     _buildAddress()),
                      _row('Nominee',     _bill['nominee_name']),
                      _row('ID Proof',    '${_bill['cust_id_proof_type'] ?? ''} ${_bill['cust_id_proof_number'] ?? ''}'),
                    ]),
                    const SizedBox(height: 12),
                    _section('Jewel Details', [
                      _row('Material',      widget.type),
                      _row('Items',         _bill['items']),
                      _row('Gross Weight',  _bill['gross_weight']?.toString()),
                      _row('Net Weight',    _bill['net_weight']?.toString()),
                      _row('Purity',        _bill['purity']?.toString()),
                    ]),
                    const SizedBox(height: 12),
                    _section('Financial Details', [
                      _row('Loan Amount',   _money(_bill['amount'])),
                      _row('Interest',      _money(_bill['interest'])),
                      _row('Doc Charge',    _money(_bill['document_charge'])),
                      _row('Given Amount',  _money(_bill['given_amount'])),
                      _row('To Give',       _money(_bill['togive_amount'])),
                      _row('Advance Paid',  _money(_bill['total_advance_amount_paid'])),
                    ]),
                    const SizedBox(height: 12),
                    _section('Dates', [
                      _row('Opening Date',  _bill['opening_date_str'] ?? _bill['opening_date']),
                      _row('Expected Close', _bill['accepted_closing_date_str'] ?? _bill['accepted_closing_date']),
                      _row('Closing Date',  _bill['closing_date_str'] ?? _bill['closing_date']),
                      _row('Created By',    _bill['created_user_id']?.toString()),
                      _row('Created At',    _bill['created_date_str'] ?? _bill['created_date']),
                    ]),
                    if (status != 'OPENED') ...[
                      const SizedBox(height: 12),
                      _section('Closing Details', [
                        _row('Got Amount',    _money(_bill['got_amount'])),
                        _row('To Get',        _money(_bill['toget_amount'])),
                        _row('Discount',      _money(_bill['discount_amount'])),
                        _row('Closed By',     _bill['closed_user_id']?.toString()),
                        _row('Closed Date',   _bill['closed_date_str'] ?? _bill['closed_date']),
                      ]),
                    ],
                    if (_bill['note'] != null && (_bill['note'] as String).isNotEmpty) ...[
                      const SizedBox(height: 12),
                      _section('Note', [
                        Padding(padding: const EdgeInsets.all(4),
                          child: Text(_bill['note'] as String,
                              style: const TextStyle(color: Colors.white70))),
                      ]),
                    ],
                    const SizedBox(height: 24),
                  ]),
                ),
    );
  }

  Widget _headerCard(String status, Color goldColor) {
    final statusColor = status == 'OPENED' ? Colors.green :
                        status == 'CLOSED'  ? Colors.orange : Colors.blue;
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: const Color(0xFF16213E),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: goldColor.withOpacity(0.4)),
      ),
      child: Column(children: [
        Icon(widget.type == 'GOLD' ? Icons.star : Icons.circle, color: goldColor, size: 36),
        const SizedBox(height: 8),
        Text(widget.billNumber, style: TextStyle(color: goldColor,
            fontSize: 22, fontWeight: FontWeight.bold)),
        const SizedBox(height: 4),
        Text(widget.companyName, style: const TextStyle(color: Colors.white54, fontSize: 13)),
        const SizedBox(height: 8),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
          decoration: BoxDecoration(
            color: statusColor.withOpacity(0.2),
            borderRadius: BorderRadius.circular(20),
            border: Border.all(color: statusColor),
          ),
          child: Text(status, style: TextStyle(color: statusColor, fontWeight: FontWeight.bold)),
        ),
      ]),
    );
  }

  Widget _section(String title, List<Widget> children) => Container(
    decoration: BoxDecoration(
      color: const Color(0xFF16213E),
      borderRadius: BorderRadius.circular(12),
    ),
    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Padding(
        padding: const EdgeInsets.fromLTRB(16, 14, 16, 8),
        child: Text(title, style: const TextStyle(color: Color(0xFFD4AF37),
            fontWeight: FontWeight.bold, fontSize: 14)),
      ),
      const Divider(color: Colors.white12, height: 1),
      ...children,
    ]),
  );

  Widget _row(String label, dynamic value) {
    if (value == null || value.toString().trim().isEmpty) return const SizedBox.shrink();
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
        SizedBox(width: 130,
            child: Text(label, style: const TextStyle(color: Colors.white54, fontSize: 13))),
        Expanded(child: Text(value.toString(),
            style: const TextStyle(color: Colors.white, fontSize: 13))),
      ]),
    );
  }

  String _buildAddress() {
    final parts = [
      _bill['door_number'], _bill['street'],
      _bill['area'], _bill['city'],
    ].where((e) => e != null && e.toString().isNotEmpty).join(', ');
    return parts;
  }

  String? _money(dynamic v) {
    if (v == null) return null;
    final d = v is num ? v.toDouble() : double.tryParse(v.toString());
    if (d == null || d == 0) return null;
    return '₹ ${_fmt.format(d)}';
  }
}
