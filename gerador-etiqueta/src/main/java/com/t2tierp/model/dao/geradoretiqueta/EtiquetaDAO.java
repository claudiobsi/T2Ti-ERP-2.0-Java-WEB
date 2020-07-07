package com.t2tierp.model.dao.geradoretiqueta;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import com.t2tierp.model.bean.geradoretiqueta.EtiquetaTemplate;
import com.t2tierp.model.dao.GenericDAO;

@Stateless
public class EtiquetaDAO extends GenericDAO<EtiquetaTemplate> {

	@SuppressWarnings("rawtypes")
	public List listaRegistros(EtiquetaTemplate template) throws Exception {
		String sql = "SELECT " + template.getCampo() + " FROM " + template.getTabela() + " WHERE " + template.getCampo() + " iLIKE " + "'%" + template.getFiltro() + "%'";

		Query query = em.createNativeQuery(sql);
		return query.getResultList();
	}

	public Map<String, List<String>> listaTabelas() throws Exception {
		Session session = em.unwrap(Session.class);
		Map<String, List<String>> tabelas = new HashMap<>();
		session.doWork(new Work() {

			@Override
			public void execute(Connection conn) throws SQLException {
				DatabaseMetaData databaseMetaData = conn.getMetaData();
				ResultSet rsTabelas = databaseMetaData.getTables(null, null, "%", new String[] { "TABLE" });
				ResultSet rsCampos;

				
				List<String> campos;

				String nomeTabela;
				while (rsTabelas.next()) {
					campos = new ArrayList<>();
					nomeTabela = rsTabelas.getString("TABLE_NAME");

					rsCampos = databaseMetaData.getColumns(null, null, nomeTabela, null);
					while (rsCampos.next()) {
						campos.add(rsCampos.getString("COLUMN_NAME"));
					}

					tabelas.put(nomeTabela, campos);
				}
				
			}
		});
		return tabelas;
	}
}
