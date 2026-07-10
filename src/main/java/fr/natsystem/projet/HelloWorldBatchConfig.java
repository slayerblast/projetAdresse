package fr.natsystem.projet;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.RecordFieldSetMapper;
import org.springframework.batch.infrastructure.item.validator.ValidatingItemProcessor;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;



@Configuration
public class HelloWorldBatchConfig {

    public record Adresse(
            String id,
            String id_fantoir,
            String numero,
            String rep,
            String nom_voie,
            String code_postal,
            String code_insee,
            String nom_commune,
            String code_insee_ancienne_commune,
            String nom_ancienne_commune,
            Double x,
            Double y,
            Double lon,
            Double lat,
            String type_position,
            String alias,
            String nom_ld,
            String libelle_acheminement,
            String nom_afnor,
            String source_position,
            String source_nom_voie,
            Integer certification_commune,
            String cad_parcelles
    ) {
    }


    // FlatFileItemReader
    /* Reader non dynamique
	@Bean
	public FlatFileItemReader<Adresse> csvReader() {
	    return new FlatFileItemReaderBuilder<Adresse>()
	            .name("adresseCsvReader")
	            .resource(new ClassPathResource("adresses-79-2026-06-22.csv"))
	            .delimited()
	            .delimiter(";")
	            .names(
	            	    "id",
	            	    "id_fantoir",
	            	    "numero",
	            	    "rep",
	            	    "nom_voie",
	            	    "code_postal",
	            	    "code_insee",
	            	    "nom_commune",
	            	    "code_insee_ancienne_commune",
	            	    "nom_ancienne_commune",
	            	    "x",
	            	    "y",
	            	    "lon",
	            	    "lat",
	            	    "type_position",
	            	    "alias",
	            	    "nom_ld",
	            	    "libelle_acheminement",
	            	    "nom_afnor",
	            	    "source_position",
	            	    "source_nom_voie",
	            	    "certification_commune",
	            	    "cad_parcelles"
	            	)
	            .fieldSetMapper(new RecordFieldSetMapper<>(Adresse.class))
	            .linesToSkip(1)
	            .build();
	}*/
    @Bean
    @StepScope
    public FlatFileItemReader<Adresse> csvDynamicReader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<Adresse>()
                .name("csvDynamicReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .delimiter(";")
                .names(
                        "id",
                        "id_fantoir",
                        "numero",
                        "rep",
                        "nom_voie",
                        "code_postal",
                        "code_insee",
                        "nom_commune",
                        "code_insee_ancienne_commune",
                        "nom_ancienne_commune",
                        "x",
                        "y",
                        "lon",
                        "lat",
                        "type_position",
                        "alias",
                        "nom_ld",
                        "libelle_acheminement",
                        "nom_afnor",
                        "source_position",
                        "source_nom_voie",
                        "certification_commune",
                        "cad_parcelles"
                )
                .fieldSetMapper(new RecordFieldSetMapper<>(Adresse.class))
                .linesToSkip(1)
                .build();
    }

    // JdbcBatchItemWriter
    @Bean
    public JdbcBatchItemWriter<Adresse> jdbcWriter(
            DataSource ds) {
        // La requete SQL va ignorer ceux dont elles n'arrive pas à inserer
        return new JdbcBatchItemWriterBuilder<Adresse>()
                .dataSource(ds)
                .sql("""   
                        INSERT INTO adresse (
                            id, id_fantoir, numero, rep, nom_voie, code_postal, code_insee,
                            nom_commune, code_insee_ancienne_commune, nom_ancienne_commune,
                            x, y, lon, lat, type_position, alias, nom_ld,
                            libelle_acheminement, nom_afnor, source_position, source_nom_voie,
                            certification_commune, cad_parcelles
                        ) VALUES (
                            :id, :id_fantoir, :numero, :rep, :nom_voie, :code_postal, :code_insee,
                            :nom_commune, :code_insee_ancienne_commune, :nom_ancienne_commune,
                            :x, :y, :lon, :lat, :type_position, :alias, :nom_ld,
                            :libelle_acheminement, :nom_afnor, :source_position, :source_nom_voie,
                            :certification_commune, :cad_parcelles
                        );
                        """)
                .beanMapped()   // :paramName -> getter du bean
                .build();
    }

    @Bean
    public ValidatingItemProcessor<Adresse> validatingProcessor(
            AdresseDuplicateValidator validator) {

        ValidatingItemProcessor<Adresse> processor =
                new ValidatingItemProcessor<>(validator);

        processor.setFilter(true);

        return processor;
    }

    // JobRepository et PlatformTransactionManager sont auto-câblés par Spring Boot
    @Bean
    public Job helloWorldJob(JobRepository jobRepository, Step helloStep) {
        return new JobBuilder("helloWorldJob", jobRepository).start(helloStep).build();
    }

    @Bean
    public Job importAdresseJob(JobRepository jobRepository, Step importAdresseStep,
                                BilanJobListener listener,DuplicationJobListener duplicationListener) {
        return new JobBuilder("importAdresseJob", jobRepository)
                .listener(listener)
                .listener(duplicationListener)
                .start(importAdresseStep)
                .build();
    }

    @Bean
    public Step helloStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("helloStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("=== Hello, Spring Batch ! ===");
                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }

    @Bean
    public Step importAdresseStep(
            JobRepository repo,
            PlatformTransactionManager tx,
            FlatFileItemReader<Adresse> csvReader,
            JdbcBatchItemWriter<Adresse> jdbcWriter,
            ValidatingItemProcessor<Adresse> validatingProcessor,
            StepProgessListener listener,
            AdresseSkipListener skipListener) {
        return new StepBuilder("importAdresseStep", repo)
                .<Adresse, Adresse>chunk(1000)
                .transactionManager(tx)
                .reader(csvReader)
                .processor(validatingProcessor)
                .writer(jdbcWriter)
                .faultTolerant()
                .skip(DataAccessException.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(listener)
                .listener(skipListener)
                .build();

    }


}
