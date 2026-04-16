class Bill {
  final String billNumber;
  final String materialType;
  final String customerName;
  final String? mobileNumber;
  final String? openingDate;
  final String? acceptedClosingDate;
  final double amount;
  final double openTakenAmount;
  final String status;
  final String? items;

  const Bill({
    required this.billNumber,
    required this.materialType,
    required this.customerName,
    this.mobileNumber,
    this.openingDate,
    this.acceptedClosingDate,
    required this.amount,
    required this.openTakenAmount,
    required this.status,
    this.items,
  });

  factory Bill.fromJson(Map<String, dynamic> json) => Bill(
        billNumber:          json['bill_number'] as String? ?? '',
        materialType:        json['jewel_material_type'] as String? ?? '',
        customerName:        json['customer_name'] as String? ?? '',
        mobileNumber:        json['mobile_number'] as String?,
        openingDate:         json['opening_date'] as String?,
        acceptedClosingDate: json['accepted_closing_date'] as String?,
        amount:              _toDouble(json['amount']),
        openTakenAmount:     _toDouble(json['open_taken_amount']),
        status:              json['status'] as String? ?? '',
        items:               json['items'] as String?,
      );

  static double _toDouble(dynamic v) {
    if (v == null) return 0.0;
    if (v is num) return v.toDouble();
    return double.tryParse(v.toString()) ?? 0.0;
  }

  bool get isGold   => materialType == 'GOLD';
  bool get isOpened => status == 'OPENED';
}
