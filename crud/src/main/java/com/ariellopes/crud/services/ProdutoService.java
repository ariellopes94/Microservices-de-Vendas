package com.ariellopes.crud.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ariellopes.crud.data.vo.ProdutoVO;
import com.ariellopes.crud.entity.Produto;
import com.ariellopes.crud.exception.ResourceNotFoundException;
import com.ariellopes.crud.repositories.ProdutoRepository;

@Service
public class ProdutoService {
	
	private final ProdutoRepository produtoRepository;
	
	@Autowired
	public ProdutoService(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}
	
	public ProdutoVO create(ProdutoVO produtoVO) {
		ProdutoVO proVoRetorno = ProdutoVO.create(produtoRepository.save(Produto.create(produtoVO)));
		return proVoRetorno;
	}
	
	public Page<ProdutoVO> findAlPage(Pageable pageable){
		var page = produtoRepository.findAll(pageable);
		return page.map(this::convertToProdutoVo);
	}
	
	private ProdutoVO convertToProdutoVo(Produto produto) {
		return ProdutoVO.create(produto);
	}
	
	public ProdutoVO findByid(Long id) {
		var entity = produtoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não existe"));
		return ProdutoVO.create(entity);
	}
	
	public ProdutoVO update(ProdutoVO produtoVO) {
		final Optional<Produto> optionalProduto = produtoRepository.findById(produtoVO.getId());
		
		if(!optionalProduto.isPresent()) {
			new ResourceNotFoundException("Produto não existe");
		}
		return ProdutoVO.create(produtoRepository.save(Produto.create(produtoVO)));
	}
	
	public void delete(Long id) {
		var entity = produtoRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("no records found for this ID"));
		produtoRepository.delete(entity);
	}
}
