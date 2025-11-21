package com.hulkhiretech.payments.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.dao.interfaces.TransactionDao;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.exception.ProcessingServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TransactionDaoImpl implements TransactionDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	
	@Override
	public TransactionEntity createTransaction(TransactionEntity txnEntity) {
		log.info("Creating TransactionEntity in DB: {}", txnEntity);

		String sql = "INSERT INTO `Transaction` (" +
				"userId, paymentMethodId, providerId, paymentTypeId, txnStatusId, " +
				"amount, currency, merchantTransactionReference, txnReference, providerReference, " +
				"errorCode, errorMessage, retryCount" +
				") VALUES (" +
				":userId, :paymentMethodId, :providerId, :paymentTypeId, :txnStatusId, " +
				":amount, :currency, :merchantTransactionReference, :txnReference, :providerReference, " +
				":errorCode, :errorMessage, :retryCount" +
				")";

		SqlParameterSource params = new BeanPropertySqlParameterSource(txnEntity);

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

		// Extract generated ID
		Number generatedId = keyHolder.getKey();
		if (generatedId != null) {
			txnEntity.setId(generatedId.intValue());
		}

		log.info("Inserted TransactionEntity with ID: {}", txnEntity.getId());
		return txnEntity;

	}



   
    @Override
    public TransactionEntity getTransactionByTxnReference(String txnReference) {

        String sql = "SELECT * FROM Transaction WHERE txnReference = :txnReference";

        Map<String, Object> params = new HashMap<>();
        params.put("txnReference", txnReference);

        
            TransactionEntity txnEntity = jdbcTemplate.queryForObject(
		        sql,
		        params,
		        new BeanPropertyRowMapper<>(TransactionEntity.class));
            
            log.info("Fetched TransactionEntity: {}", txnEntity);
			return txnEntity;
         
       
        }




    @Override
    public void updateTransaction(TransactionEntity txnEntity) {

        String sql1 = "UPDATE `Transaction` " +
                     "SET txnStatusId = :txnStatusId " +
                     "WHERE id = :id";
        
        String sql = "UPDATE `Transaction` "
                + "SET txnStatusId = :txnStatusId, "
                + "providerReference = :providerReference "
                + "WHERE id = :id";


        Map<String, Object> params = new HashMap<>();
        params.put("txnStatusId", txnEntity.getTxnStatusId());
        params.put("id", txnEntity.getId());
        params.put("providerReference", txnEntity.getProviderReference());

        int rowsAffected = jdbcTemplate.update(sql, params);

        if (rowsAffected == 0) {
            log.error("Failed to update TransactionEntity with ID: {}", txnEntity.getId());
            throw new ProcessingServiceException(
                    ErrorCodeEnum.UNABLE_TO_UPDATE_TRANSACTION.getErrorCode(),
                    ErrorCodeEnum.UNABLE_TO_UPDATE_TRANSACTION.getErrorMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    

}
	
	 


