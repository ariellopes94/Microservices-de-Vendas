package com.ariellopes.crud.controler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties.Build;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ariellopes.crud.data.vo.ProdutoVO;
import com.ariellopes.crud.services.ProdutoService;



@RestController
@RequestMapping("/produto")
public class ProdutoControler {
	
	private final ProdutoService produtoService;
	private final PagedResourcesAssembler<ProdutoVO>assembler;
	
	@Autowired
	public ProdutoControler(ProdutoService produtoService, PagedResourcesAssembler<ProdutoVO> assembler) {
		this.produtoService = produtoService;
		this.assembler = assembler;
	}
	
	@GetMapping(value = "/{id}", produces = {"application/json","application/xml","application/x-yaml"})
	public ProdutoVO findById(@PathVariable("id") Long id) {
		ProdutoVO produtoVo = produtoService.findByid(id);
		produtoVo.add(linkTo(methodOn(ProdutoControler.class).findById(id)).withSelfRel());
		return produtoVo;
	}
	
	@GetMapping(produces = {"application/json","application/xml","application/x-yaml"})
	public ResponseEntity<?> findAll (@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "12") int limit,
			@RequestParam(value = "direction", defaultValue = "asc") String direction) {
	
		var sorDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		
		Pageable pageable = PageRequest.of(page, limit, Sort.by(sorDirection,"nome"));
		
		Page<ProdutoVO> produtos = produtoService.findAlPage(pageable);
		produtos.stream()
				.forEach(p -> p.add(linkTo(methodOn(ProdutoControler.class).findById(p.getId())).withSelfRel()));
		PagedModel<EntityModel<ProdutoVO>> pagedModel = assembler.toModel(produtos);
		
		return new ResponseEntity<>(pagedModel, HttpStatus.OK);
	}
	
	@PostMapping(produces = {"application/json","application/xml","application/x-yaml"},
				 consumes = {"application/json","application/xml","application/x-yaml"})
	public ProdutoVO create(@RequestBody ProdutoVO produtoVo) {
		ProdutoVO proVo = produtoService.create(produtoVo);
		proVo.add(linkTo(methodOn(ProdutoControler.class).findById(proVo.getId())).withSelfRel());
		return proVo;
	}
	
	@PutMapping(produces = {"application/json","application/xml","application/x-yaml"},
			 consumes = {"application/json","application/xml","application/x-yaml"})
	public ProdutoVO update(@RequestBody ProdutoVO produtoVo) {
		ProdutoVO proVo = produtoService.update(produtoVo);
		proVo.add(linkTo(methodOn(ProdutoControler.class).findById(produtoVo.getId())).withSelfRel());
		return produtoVo;
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id){
		produtoService.delete(id);
		return ResponseEntity.ok().build();
	}
}
