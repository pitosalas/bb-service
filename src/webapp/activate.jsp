<%@ page import="com.salas.bbservice.service.account.AccountService,
                 com.salas.bbservice.service.account.AccountNotRegisteredException,
                 com.salas.bbservice.service.account.AccountHandler"%>
 <%--
    Activation of user account.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String email = request.getParameter("email");
  final String idStr = request.getParameter("accountid");

  String message = (new AccountHandler()).activateAccount(email, idStr);

  if (message.equals("")) message = "Thank you for activating your account. " +
      "It is now fully functional and ready to be used by BlogBridge.";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
  <TITLE>BlogBridge: Account Activation</TITLE>
  <META http-equiv=Content-Type content="text/html; charset=iso-8859-1">
</HEAD>
<STYLE TYPE="text/css">
  p { margin: 100px auto; width: 50%; font: 20px Verdana, Tahoma, Arial, sans-serif; text-align: justify; line-height: 30px;}
</STYLE>
<BODY>
  <P><%= message %>
</BODY>
</HTML>
