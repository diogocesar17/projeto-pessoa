package com.projetopessoa.controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import com.projetopessoa.data.CreateDataSourceForJdbcTemplate;
import com.projetopessoa.models.Pessoa;

@Controller
public class PessoaController {

	
	@RequestMapping(value="/cadastrarPessoa", method = RequestMethod.GET)
	public String form() {
		return "pessoa/formPessoa";
	}
	
	@RequestMapping(value="/cadastrarPessoa", method = RequestMethod.POST)
	public String form(Pessoa pessoa) {
		JdbcTemplate getJdbcTemplate = new JdbcTemplate();
        getJdbcTemplate.setDataSource(CreateDataSourceForJdbcTemplate.getDataSource());
        StringBuilder query = new StringBuilder();

        query.append("insert into pessoa(codigo, nome, cpf, altura, peso, sexo, data_nasc) values (?, ?, ?, ?, ?, ?, ?)");

        getJdbcTemplate.update(query.toString(),
                pessoa.getCodigo(),
                pessoa.getNome(),
                pessoa.getCpf(),
                pessoa.getAltura(),
                pessoa.getPeso(),
                pessoa.getSexo(),
                pessoa.getDataNasc());
		return "redirect:/pessoas";
	}
	
	@RequestMapping(value="/deletarPessoa", method=RequestMethod.GET)
    public String deletarPessoa(long id) throws Exception {
        JdbcTemplate getJdbcTemplate = new JdbcTemplate();
        getJdbcTemplate.setDataSource(CreateDataSourceForJdbcTemplate.getDataSource());
        StringBuilder query = new StringBuilder();

        query.append("delete from pessoa where codigo = ?");
        getJdbcTemplate.update(query.toString(), id);
        return "redirect:/pessoas";
    }
	
	@RequestMapping("/pessoas")
    public ModelAndView listaPessoas() throws Exception {
		JdbcTemplate getJdbcTemplate = new JdbcTemplate();
        getJdbcTemplate.setDataSource(CreateDataSourceForJdbcTemplate.getDataSource());
        StringBuilder query = new StringBuilder();

        query.append("select * from pessoa");

         
        ModelAndView modelView = new ModelAndView("index");
		Iterable<Pessoa> pessoas = getJdbcTemplate.query(query.toString(), new PessoaController.PessoaRowMapper());
		modelView.addObject("pessoas", pessoas);
		return modelView;
    }
	
	@RequestMapping("/")
	public ModelAndView pesquisaPessoa(@RequestParam(name = "nomePesquisa") String nomePesquisa) {
		JdbcTemplate getJdbcTemplate = new JdbcTemplate();
        getJdbcTemplate.setDataSource(CreateDataSourceForJdbcTemplate.getDataSource());
        StringBuilder query = new StringBuilder();
        query.append("select * from pessoa where nome like ?");
        ModelAndView modelView = new ModelAndView("pessoa/pesquisarPessoa");
		Iterable<Pessoa> buscaPessoas = getJdbcTemplate.query(query.toString(), new PessoaController.PessoaRowMapper(), "%" + nomePesquisa +"%");
		modelView.addObject("buscaPessoas", buscaPessoas);
		return modelView;
	}

	@RequestMapping(value="/atualizarPessoa", method=RequestMethod.GET)
    public ModelAndView atualizarPessoa(long id) {
    	JdbcTemplate getJdbcTemplate = new JdbcTemplate();
    	getJdbcTemplate.setDataSource(CreateDataSourceForJdbcTemplate.getDataSource());
    	StringBuilder query = new StringBuilder();
    	query.append("select * from pessoa where codigo = ?");
    	ModelAndView modelView = new ModelAndView("pessoa/updatePessoa");
        Pessoa pessoa = getJdbcTemplate.queryForObject(query.toString(), new EmployeeMapper(), id);
        System.out.print("Recuperou - " + pessoa.getCodigo());
        modelView.addObject("pessoa", pessoa);
        return modelView;
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute("pessoa") Pessoa pessoa) {
    	System.out.print("Salvou - " + pessoa.getCodigo());
		JdbcTemplate getJdbcTemplate = new JdbcTemplate();
        getJdbcTemplate.setDataSource(CreateDataSourceForJdbcTemplate.getDataSource());
        StringBuilder query = new StringBuilder();

        query.append("update pessoa set nome = ? where codigo = ?");
        getJdbcTemplate.update(query.toString(),
                pessoa.getNome(),
                pessoa.getCodigo());
		return "redirect:/pessoas";
    }
    
    private static final class EmployeeMapper implements RowMapper<Pessoa> {
        public Pessoa mapRow(ResultSet rs, int rowNum) throws SQLException {
        	Pessoa dto = new Pessoa();
            dto.setCodigo(rs.getLong("codigo"));
            dto.setNome(rs.getString("nome"));
            dto.setCpf(rs.getString("cpf"));
            dto.setSexo(rs.getString("sexo"));
            dto.setDataNasc(rs.getDate("data_nasc"));
            dto.setAltura(rs.getDouble("altura"));
            dto.setPeso(rs.getDouble("peso"));
          return dto;
        }
      }
    
	public final class PessoaRowMapper implements RowMapper {
        public Pessoa mapRow(ResultSet rs, int rowNum) {
            Pessoa dto = new Pessoa();

            try {
                dto.setCodigo(rs.getLong("codigo"));
                dto.setNome(rs.getString("nome"));
                dto.setCpf(rs.getString("cpf"));
                dto.setSexo(rs.getString("sexo"));
                dto.setDataNasc(rs.getDate("data_nasc"));
                dto.setAltura(rs.getDouble("altura"));
                dto.setPeso(rs.getDouble("peso"));
            } catch (SQLException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar os produtos");
            }
            return dto;
        }
    }
}
