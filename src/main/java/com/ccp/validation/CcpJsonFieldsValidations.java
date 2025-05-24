package com.ccp.validation;

import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.validation.annotations.AllowedValues;
import com.ccp.validation.annotations.ArrayNumbers;
import com.ccp.validation.annotations.ArraySize;
import com.ccp.validation.annotations.ArrayTextSize;
import com.ccp.validation.annotations.Day;
import com.ccp.validation.annotations.ObjectNumbers;
import com.ccp.validation.annotations.ObjectTextSize;
import com.ccp.validation.annotations.Regex;
import com.ccp.validation.annotations.SimpleObject;
import com.ccp.validation.annotations.CcpJsonFieldsValidation;
import com.ccp.validation.annotations.Year;
import com.ccp.validation.enums.AllowedValuesValidations;
import com.ccp.validation.enums.BoundValidations;
import com.ccp.validation.enums.SimpleObjectValidations;

public class CcpJsonFieldsValidations {
	// Valida o conteúdo das informações do JSON com os campos da tabela que receberá os dados
	public static void validate(Class<?> clazz, Map<String, Object> map, String featureName) {
		// Verifica se há algum campo do JSON que não está presente na tabela
		boolean isNotPresent = clazz.isAnnotationPresent(CcpJsonFieldsValidation.class) == false;
		// Se algum campo do JSON não estiver na tabela, retorna.
		if(isNotPresent) {
			return;
		}
		// Se todos campos do JSON estiverem na tabela, dá continuidade organizando
		CcpJsonFieldsValidation rules = clazz.getAnnotation(CcpJsonFieldsValidation.class);
		// Organiza os dados e executa o método de validação
		validate(rules, map, featureName);
	}
	// Após a organização do método, é chamado aqui para execução
	public static void validate(CcpJsonFieldsValidation rules, Map<String, Object> map, String featureName) {
		// Recebe o JSON mapeado
		CcpJsonRepresentation json = new CcpJsonRepresentation(map);
		// ???
		Class<?> rulesClass = rules.rulesClass();
		// ???
		rules = rulesClass.isAnnotationPresent(CcpJsonFieldsValidation.class) ? rulesClass.getAnnotation(CcpJsonFieldsValidation.class) : rules;
		// Cria variável de evidência em formato JSON vazia
		CcpJsonRepresentation evidences = CcpOtherConstants.EMPTY_JSON;
		// Validando os limites
		evidences = validateBounds(rules, json, evidences);
		// Validando restrições
		evidences = validateRestricted(rules, json, evidences);
		// Validações simples 
		evidences = simpleValidation(rules, json, evidences);
		// Acrescenta a relação de erros
		CcpJsonRepresentation errors = evidences.getInnerJson("errors");
		// Carrega a variável booleana vazia		--		Não está faltando um if do tipo se errors for vazio então carrega noErrors com errors.isEmpty()? 
		boolean noErrors = errors.isEmpty();
		// Caso noErros seja verdadeiro, retorna
		if (noErrors) {
			return;
		}
		// Se noErrors for falso continua carregando a variável de especificação
		CcpJsonRepresentation specification = getSpecification(featureName, rules);
		// Incrementa a variável de evidência com a especificação
		CcpJsonRepresentation result = evidences.put("specification", specification).put("json", json);
		// Se der algum erro, será redirecionado à classe CcpJsonInvalid()
		throw new CcpJsonInvalid(result);
	}

	private static CcpJsonRepresentation simpleValidation(
			CcpJsonFieldsValidation rules, 
			CcpJsonRepresentation json,
			CcpJsonRepresentation result
			) {
		SimpleObject[] simples = rules.simpleObject();

		for (SimpleObject validation : simples) {
			
			SimpleObjectValidations rule = validation.rule();
			result = rule.validate(json, result, validation);
		}

		return result;
	}

	private static CcpJsonRepresentation getSpecification(String featureName, CcpJsonFieldsValidation rules) {

		CcpJsonRepresentation specification = CcpOtherConstants.EMPTY_JSON;
		
		AllowedValues[] allowedValues = rules.allowedValues();
		
		specification = specification.put("featureName", featureName);
		for (AllowedValues dumb : allowedValues) {

			String[] restrictedValues = dumb.allowedValues();
			AllowedValuesValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(AllowedValues.class, rule);
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);
			specification = specification.addToItem(completeRuleName, "restrictedValues", restrictedValues);
		}
		
		ArrayNumbers[] arrayNumbers = rules.arrayNumbers();
		
		for (ArrayNumbers dumb : arrayNumbers) {
			BoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			double bound = dumb.bound();
			
			String completeRuleName = getCompleteRuleName(ArrayNumbers.class, rule);
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);
			specification = specification.addToItem(completeRuleName, "bound", bound);
		}
		
		ArraySize[] arraySize = rules.arraySize();
		
		for (ArraySize dumb : arraySize) {
			double bound = dumb.bound();
			BoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(ArraySize.class, rule);
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);
			specification = specification.addToItem(completeRuleName, "bound", bound);

		}
		ArrayTextSize[] arrayTextSize = rules.arrayTextSize();
		
		for (ArrayTextSize dumb : arrayTextSize) {
			double bound = dumb.bound();
			BoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(ArrayTextSize.class, rule);
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);
			specification = specification.addToItem(completeRuleName, "bound", bound);
		}
		
		Day[] day = rules.day();
		
		for (Day dumb : day) {
			double bound = dumb.bound();
			BoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(Day.class, rule);
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);
			specification = specification.addToItem(completeRuleName, "bound", bound);

		}
		ObjectNumbers[] objectNumbers = rules.objectNumbers();
		for (ObjectNumbers dumb : objectNumbers) {
			double bound = dumb.bound();
			BoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(ObjectNumbers.class, rule);
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);
			specification = specification.addToItem(completeRuleName, "bound", bound);

		}
		ObjectTextSize[] objectTextSize = rules.objectTextSize();
		for (ObjectTextSize dumb : objectTextSize) {
			double bound = dumb.bound();
			BoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(ObjectTextSize.class, rule);
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);
			specification = specification.addToItem(completeRuleName, "bound", bound);

		}
		Regex[] regex = rules.regex();
		
		for (Regex dumb : regex) {
			String value = dumb.value().value;
			String[] fields = dumb.fields();
			
			String completeRuleName = Regex.class.getSimpleName();
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);
			specification = specification.addToItem(completeRuleName, "value", value);

		}
		
		SimpleObject[] simpleObject = rules.simpleObject();
		
		for (SimpleObject dumb : simpleObject) {
			SimpleObjectValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(SimpleObject.class, rule);
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);

		}
		
		Year[] year = rules.year();
		for (Year dumb : year) {
			double bound = dumb.bound();
			BoundValidations rule = dumb.rule();
			String[] fields = dumb.fields();
			
			String completeRuleName = getCompleteRuleName(Year.class, rule);
			specification = specification.addToItem(completeRuleName, "evaluatedFields", fields);
			specification = specification.addToItem(completeRuleName, "bound", bound);

		}
		return specification;
	}
	
	public static String getCompleteRuleName(Class<?> ruleClazz, BoundValidations rule) {
		String completeRuleName = getCompleteRuleName(ruleClazz, (Enum<?>)rule);
		return completeRuleName;
	}

	private static CcpJsonRepresentation validateRestricted(CcpJsonFieldsValidation rules, CcpJsonRepresentation json,
			CcpJsonRepresentation result) {
		AllowedValues[] restricteds = rules.allowedValues();

		for (AllowedValues validation : restricteds) {
			String[] restrictedValues = validation.allowedValues();
			String[] fields = validation.fields();
			AllowedValuesValidations rule = validation.rule();
			
			String completeRuleName = getCompleteRuleName(AllowedValues.class, rule);
			
			CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON;
			
			for (String field : fields) {
				
				boolean validJson = rule.isValidJson(json, restrictedValues, field);
				
				if(validJson) {
					continue;
				}
				errors = errors.put("restrictedValues", restrictedValues);
				
				boolean containsKey = json.containsField(field) == false;
				if(containsKey) {
					continue;
				}
				
				Object value = json.get(field);
				CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
						.put("name", field)
						.put("value", value)
						;
				errors = errors.addToList("wrongFields", fieldDetails);
				result = result.addToItem("errors", completeRuleName, errors);
			}
		}
		return result;
	}

	private static CcpJsonRepresentation addErrorDetail(
			CcpJsonRepresentation result, 
			CcpJsonRepresentation json, 
			Class<?> ruleClass, 
			Object bound, 
			BoundValidations rule, 
			String... fields) {
		
		String completeRuleName = getCompleteRuleName(ruleClass, (Enum<?>)rule);
		
		CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON;
		
		for (String field : fields) {
			
			double boundValue = Double.valueOf("" + bound);
			
			boolean validJson = rule.isValidJson(json, boundValue, field);
			
			if(validJson) {
				continue;
			}
			errors = errors.put("bound", bound);
			boolean fieldIsNotPresent = json.containsAllFields(field) == false;
			if(fieldIsNotPresent) {
				CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
						.put("name", field)
						;
				errors = errors.addToList("wrongFields", fieldDetails);
				result = result.addToItem("errors", completeRuleName, errors);
				continue;
			}
			Object value = json.get(field);
			CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
					.put("name", field)
					.put("value", value)
					;
			errors = errors.addToList("wrongFields", fieldDetails);
			result = result.addToItem("errors", completeRuleName, errors);
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
			ArrayNumbers[] x1 = rules.arrayNumbers();

			for (ArrayNumbers validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				BoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, ArrayNumbers.class, bound, rule, fields);
			}
			
		}

		{
			ArraySize[] x1 = rules.arraySize();

			for (ArraySize validation : x1) {

				double bound = validation.bound();
				String[] fields = validation.fields();
				BoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, ArraySize.class, bound, rule, fields);
			}

		}
		{
			ArrayTextSize[] x1 = rules.arrayTextSize();

			for (ArrayTextSize validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				BoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, ArrayTextSize.class, bound, rule, fields);
			}

		}
		{
			ObjectNumbers[] x1 = rules.objectNumbers();

			for (ObjectNumbers validation : x1) {

				double bound = validation.bound();
				String[] fields = validation.fields();
				BoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, ObjectNumbers.class, bound, rule, fields);
			}

		}
		{
			ObjectTextSize[] x1 = rules.objectTextSize();

			for (ObjectTextSize validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				BoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, ObjectTextSize.class, bound, rule, fields);
			}

		}
		{
			Day[] x1 = rules.day();

			for (Day validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				BoundValidations rule = validation.rule();
				result = addErrorDetail(result, json, Day.class, bound, rule, fields);
			}

		}
		{
			Year[] x1 = rules.year();

			for (Year validation : x1) {
				double bound = validation.bound();
				String[] fields = validation.fields();
				BoundValidations rule = validation.rule();

				result = addErrorDetail(result, json, Year.class, bound, rule, fields);
			}

		}
		
		{
			Regex[] x1 = rules.regex();
			CcpJsonRepresentation errors = CcpOtherConstants.EMPTY_JSON;
			for (Regex validation : x1) {
				String[] fields = validation.fields();
				String regex = validation.value().value;
				for (String field : fields) {
					boolean validJson = json.itIsTrueThatTheFollowingFields(field).ifTheyAreAll()
							.textsThenEachOneMatchesWithTheFollowingRegex(regex);

					if (validJson) {
						continue;
					}
					errors = errors.put("regex", regex);
					Object value = json.get(field);
					CcpJsonRepresentation fieldDetails = CcpOtherConstants.EMPTY_JSON
							.put("name", field)
							.put("value", value)
							;
					errors = errors.addToList("wrongFields", fieldDetails);
					result = result.addToItem("errors", "regex", errors);
					
				}
			}
		}
		
		return result;
	}


}
