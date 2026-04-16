class Company {
  final int id;
  final String name;
  final String city;
  final String? area;
  final String? mobileNumber;
  final String? type;
  final String? dayOrMonthlyInterest;
  final String status;

  const Company({
    required this.id,
    required this.name,
    required this.city,
    this.area,
    this.mobileNumber,
    this.type,
    this.dayOrMonthlyInterest,
    required this.status,
  });

  factory Company.fromJson(Map<String, dynamic> json) => Company(
        id:                   json['id'] as int,
        name:                 json['name'] as String,
        city:                 json['city'] as String? ?? '',
        area:                 json['area'] as String?,
        mobileNumber:         json['mobile_number'] as String?,
        type:                 json['type'] as String?,
        dayOrMonthlyInterest: json['day_or_monthly_interest'] as String?,
        status:               json['status'] as String? ?? 'ACTIVE',
      );
}
