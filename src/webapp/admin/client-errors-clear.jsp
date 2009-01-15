<%@ page import="com.salas.bbservice.persistence.IClientErrorDao,
                 com.salas.bbservice.persistence.DaoConfig"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  IClientErrorDao errorDao = (IClientErrorDao)DaoConfig.getDao(IClientErrorDao.class);
  errorDao.deleteAll();
%>

<%@ include file="client-errors.jsp"%>
