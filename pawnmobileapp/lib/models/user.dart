class User {
  final int userId;
  final String userName;
  final String employeeName;
  final int roleId;
  final String roleName;
  final String token;

  const User({
    required this.userId,
    required this.userName,
    required this.employeeName,
    required this.roleId,
    required this.roleName,
    required this.token,
  });

  factory User.fromJson(Map<String, dynamic> json) => User(
        userId:       json['userId'] as int,
        userName:     json['userName'] as String,
        employeeName: json['employeeName'] as String,
        roleId:       json['roleId'] as int,
        roleName:     json['roleName'] as String,
        token:        json['token'] as String,
      );
}
