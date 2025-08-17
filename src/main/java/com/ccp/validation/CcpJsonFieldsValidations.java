package com.ccp.validation;

import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpErrorJsonFieldsInvalid;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.validation.annotations.CcpAllowedValues;
import com.ccp.validation.annotations.CcpArrayNumbers;
import com.ccp.validation.annotations.CcpArraySize;
import com.ccp.validation.annotations.CcpArrayTextSize;
import com.ccp.validation.annotations.CcpDay;
import com.ccp.validation.annotations.CcpObjectNumbers;
import com.ccp.validation.annotations.CcpObjectTextSize;
import com.ccp.validation.annotations.CcpRegex;
import com.ccp.validation.annotations.CcpSimpleObject;
import com.ccp.validation.annotations.CcpJsonFieldsValidation;
import com.ccp.validation.annotations.CcpYear;
import com.ccp.validation.enums.CcpAllowedValuesValidations;
import com.ccp.validation.enums.CcpBoundValidations;
import com.ccp.validation.enums.CcpSimpleObjectValidations;
public class CcpJsonFieldsValidations {
	enum JsonFieldNames implements CcpJsonFieldName{
		errors, specification, regex, value, name, wrongFields, bound, restrictedValues, evaluatedFields, json, featureName, 
	}
	// Valida o conteúdo das informações do JSON com os campos da tabela que receberá os dados
	public static void validate(Class<?> clazz, Map<String, Object> map, String featureName) {
		// Verifica se há algum campo do JSON que não está presente na tabela
		boolean isNotPresent = clazz.isAnnotationPresent(CcpJsonFieldsValidation.class) == false;
		// Se algum campo do JSON não estiver na tabela, retorna.
		if(isNotPresent) {
			return;
		}
		CcpJsonFieldsValidation rules = clazz.getAnnotation(CcpJsonFieldsValidation.class);
		// Organiza os dados e executa o método de validação
		validate(rules, map, featureName);
	}
	// Após a organização do método, é chamado aqui para execução
	public static void validate(CcpJsonFieldsValidation rules, Map<String, Object> map, String featureName) {
		// Recebe o JSON mapeado
		CcpJsonRepresentation jsn = new CcpJsonRepresentation(map);
		// ???
		Class<?> rulesClass = rules.rulesClass();
		rules = rulesClass.isAnnotationPresent(CcpJsonFieldsValidation.class) ? rulesClass.getAnnotation(CcpJsonFieldsValidation.class) : rules;
		// Cria variável de evidência em formato JSON vazia
		CcpJsonRepresentation evidences = CcpOtherConstants.EMPTY_JSON;
		// Validando os limites
		evidences = validateBounds(rules, jsn, evidences);
		// Validando restrições
		evidences = validateRestricted(rules, jsn, evidences);
		// Validações simples 
		evidences = simpleValidation(rules, jsn, evidences);
		// Acrescenta a relação de erros
		CcpJsonRepresentation errors = evidences.getInnerJson(JsonFieldNames.errors);
		// Carrega a variável booleana vazia		--		Não está faltando um if do tipo se errors for vazio então carrega noErrors com errors.isEmpty()? 
		boolean noErrors = errors.isEmpty();
		// Caso noErros seja verdadeiro, retorna
		if (noErrors) {
			return;
		}
		// Se noErrors for falso continua carregando a variável de especificação
		CcpJsonRepresentation specification = getSpecification(featureName, rules);
		// Incrementa a variável de evidência com a especificação
		CcpJsonRepresentation result = evidences
				.put(JsonFieldNames.specification, specification)
				.put(JsonFieldNames.json, jsn);
		// Se der algum erro, será redirecionado à classe CcpJsonInvalid()
		throw new CcpErrorJsonFieldsInvalid(result);
	}

	private static CcpJsonRepresentation simpleValidation(
			CcpJsonFieldsValidation rules, 
			CcpJsonRepresentation json,
			CcpJsonRepresentation result
			) {
		CcpSimpleObject[] simples = rules.simpleObject();

		for (CcpSimpleObject validation : simples) {
			
			CcpSimpleObjectValidations rule = validation.rule();
			result = rule.validate(json, result, validation);
		}

		return result;
	}

	private static CcpJsonRepresentation getSpecification(String featureName, CcpJsonFieldsValidation rules) {

		CcpJsonRepresentation specification = CcpOtherConstants.EMPTY_JSON;
		
		CcpAllowedValues[] allowedValues = rules.allowedValues();
		
		specification = specification.put(JsonFieldNames.featureName, featureName);
		for (CcpAllowedValues dumb : allowedValues) {

			String[] restrictedValues = dumb.allowedValues();
			CcpAllowedValuesValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(CcpAllowedValues.class, rule);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.restrictedValues, restrictedValues);
		}
		
		CcpArrayNumbers[] arrayNumbers = rules.arrayNumbers();
		
		for (CcpArrayNumbers dumb : arrayNumbers) {
			CcpBoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			double bound = dumb.bound();
			
			String completeRuleName = getCompleteRuleName(CcpArrayNumbers.class, rule);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.bound, bound);
		}
		
		CcpArraySize[] arraySize = rules.arraySize();
		
		for (CcpArraySize dumb : arraySize) {
			double bound = dumb.bound();
			CcpBoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(CcpArraySize.class, rule);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.bound, bound);

		}
		CcpArrayTextSize[] arrayTextSize = rules.arrayTextSize();
		
		for (CcpArrayTextSize dumb : arrayTextSize) {
			double bound = dumb.bound();
			CcpBoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(CcpArrayTextSize.class, rule);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.bound, bound);
		}
		
		CcpDay[] day = rules.day();
		
		for (CcpDay dumb : day) {
			double bound = dumb.bound();
			CcpBoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(CcpDay.class, rule);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.bound, bound);

		}
		CcpObjectNumbers[] objectNumbers = rules.objectNumbers();
		for (CcpObjectNumbers dumb : objectNumbers) {
			double bound = dumb.bound();
			CcpBoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(CcpObjectNumbers.class, rule);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.bound, bound);

		}
		CcpObjectTextSize[] objectTextSize = rules.objectTextSize();
		for (CcpObjectTextSize dumb : objectTextSize) {
			double bound = dumb.bound();
			CcpBoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(CcpObjectTextSize.class, rule);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.bound, bound);

		}
		CcpRegex[] regex = rules.regex();
		
		for (CcpRegex dumb : regex) {
			String value = dumb.value();
			String[] fields = dumb.fields();
			
			String completeRuleName = CcpRegex.class.getSimpleName();
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.value, value);

		}
		
		CcpSimpleObject[] simpleObject = rules.simpleObject();
		
		for (CcpSimpleObject dumb : simpleObject) {
			CcpSimpleObjectValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(CcpSimpleObject.class, rule);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);

		}
		
		CcpYear[] year = rules.year();
		for (CcpYear dumb : year) {
			double bound = dumb.bound();
			CcpBoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(CcpYear.class, rule);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.evaluatedFields, fields);
			specification = specification.addToItem(completeRuleName, JsonFieldNames.bound, bound);

		}
		return specification;
	}
	
	public static String getCompleteRuleName(Class<?> ruleClazz, CcpBoundValidations rule) {
		String completeRuleName = getCompleteRuleName(ruleClazz, (Enum<?>)rule);
		return completeRuleName;
	}

	private static CcpJsonRepresentation validateRestricted(CcpJsonFieldsValidation rules, CcpJsonRepresentation json,
			CcpJsonRepresentation result) {
		CcpAllowedValues[] restricteds = rules.allowedValues();

		for (CcpAllowedValues validation : restricteds) {
			String[] restrictedValues = validation.allowedValues();
			String[] fields = validation.fields();
			CcpAllowedValuesValidations rule = validation.rule();
			
			String completeRuleName = getCompleteRuleName(CcpAllowedValues.class, rule);
			
			CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON;
			
			for (String field : fields) {
				
				boolean validJson = rule.isValidJson(json, restrictedValues, field);
				
				if(validJson) {
					continue;
				}
				errors = errors.put(JsonFieldNames.restrictedValues, restrictedValues);
				
				boolean containsKey = json.getDynamicVersion().containsField(field) == false;
				if(containsKey) {
					continue;
				}
				
				Object value = json.getDynamicVersion().get(field);
				CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
						.put(JsonFieldNames.name, field)
						.put(JsonFieldNames.value, value)
						;
				errors = errors.addToList(JsonFieldNames.wrongFields, fieldDetails);
				result = result.addToItem(JsonFieldNames.errors, completeRuleName, errors);
			}
		}
		return result;
	}

	private static CcpJsonRepresentation addErrorDetail(
			CcpJsonRepresentation result, 
			CcpJsonRepresentation json, 
			Class<?> ruleClass, 
			Object bound, 
			CcpBoundValidations rule, 
			String... fields) {
		
		String completeRuleName = getCompleteRuleName(ruleClass, (Enum<?>)rule);
		
		CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON;
		
		for (String field : fields) {
			
			double boundValue = Double.valueOf("" + bound);
			
			boolean validJson = rule.isValidJson(json, boundValue, field);
			
			if(validJson) {
				continue;
			}
			errors = errors.put(JsonFieldNames.bound, bound);
			boolean fieldIsNotPresent = json.getDynamicVersion().containsAllFields(field) == false;
			if(fieldIsNotPresent) {
				CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
						.put(JsonFieldNames.name, field)
						;
				errors = errors.addToList(JsonFieldNames.wrongFields, fieldDetails);
				result = result.addToItem(JsonFieldNames.errors, completeRuleName, errors);
				continue;
			}
			Object value = json.getDynamicVersion().get(field);
			CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
					.put(JsonFieldNames.name, field)
					.put(JsonFieldNames.value, value)
					;
			errors = errors.addToList(JsonFieldNames.wrongFields, fieldDetails);
			result = result.addToItem(JsonFieldNames.errors, completeRuleName, errors);
		}
		
		return result;
	}

	public static String getCompleteRuleName(Class<?> ruleClazz, Enum<?> enumClass) {
		
		String ruleClassName = ruleClazz.getSimpleName();
		
		String enumName = enumClass.name();

		String completeRuleName = ruleClassName + "." + enumName;
		
		return completeRuleName;
	}	
	
	private static CcpJsonRepresentation validateBounds(
			CcpJsonFieldsValidation rules, 
			CcpJsonRepresentation json,
			CcpJsonRepresentation result) {

		{
			CcpArrayNumbers[] x1 = rules.arrayNumbers();

			for (CcpArrayNumbers validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				CcpBoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, CcpArrayNumbers.class, bound, rule, fields);
			}
			
		}

		{
			CcpArraySize[] x1 = rules.arraySize();

			for (CcpArraySize validation : x1) {

				double bound = validation.bound();
				String[] fields = validation.fields();
				CcpBoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, CcpArraySize.class, bound, rule, fields);
			}

		}
		{
			CcpArrayTextSize[] x1 = rules.arrayTextSize();

			for (CcpArrayTextSize validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				CcpBoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, CcpArrayTextSize.class, bound, rule, fields);
			}

		}
		{
			CcpObjectNumbers[] x1 = rules.objectNumbers();

			for (CcpObjectNumbers validation : x1) {

				double bound = validation.bound();
				String[] fields = validation.fields();
				CcpBoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, CcpObjectNumbers.class, bound, rule, fields);
			}

		}
		{
			CcpObjectTextSize[] x1 = rules.objectTextSize();

			for (CcpObjectTextSize validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				CcpBoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, CcpObjectTextSize.class, bound, rule, fields);
			}

		}
		{
			CcpDay[] x1 = rules.day();

			for (CcpDay validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				CcpBoundValidations rule = validation.rule();
				result = addErrorDetail(result, json, CcpDay.class, bound, rule, fields);
			}

		}
		{
			CcpYear[] x1 = rules.year();

			for (CcpYear validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				CcpBoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, CcpYear.class, bound, rule, fields);
			}

		}
		
		{
			CcpRegex[] x1 = rules.regex();
			CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON;
			for (CcpRegex validation : x1) {
				String[] fields = validation.fields();
				String regex = validation.value();
				for (String field : fields) {
					boolean validJson = json.getDynamicVersion().itIsTrueThatTheFollowingFields(field).ifTheyAreAll()
							.textsThenEachOneMatchesWithTheFollowingRegex(regex);

					if (validJson) {
						continue;
					}
					errors = errors.put(JsonFieldNames.regex, regex);
					Object value = json.getDynamicVersion().get(field);
					CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
							.put(JsonFieldNames.name, field)
							.put(JsonFieldNames.value, value)
							;
					errors = errors.addToList(JsonFieldNames.wrongFields, fieldDetails);
					result = result.addToItem(JsonFieldNames.errors, JsonFieldNames.regex, errors);
					
				}
			}
		}
		
		return result;
	}


}
