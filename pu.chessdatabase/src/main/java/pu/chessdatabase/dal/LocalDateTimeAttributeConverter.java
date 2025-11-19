package pu.chessdatabase.dal;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Zie https://thorben-janssen.com/persist-localdate-localdatetime-jpa/
 */
@Converter( autoApply = true )
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp>
{
@Override
public Timestamp convertToDatabaseColumn( LocalDateTime aLocalDateTime )
{
	return ( aLocalDateTime == null ? null : Timestamp.valueOf( aLocalDateTime ) );
}
@Override
public LocalDateTime convertToEntityAttribute( Timestamp aTimeStamp )
{
	return ( aTimeStamp == null ? null : aTimeStamp.toLocalDateTime() );
}

}