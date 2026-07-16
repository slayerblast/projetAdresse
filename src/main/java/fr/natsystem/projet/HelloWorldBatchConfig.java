package fr.natsystem.projet;

import javax.sql.DataSource;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidatingItemProcessor;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration
public class HelloWorldBatchConfig {

    public record Adresse(
            @NotBlank String id,
            String id_fantoir,
            @NotBlank String numero,
            String rep,
            @NotBlank String nom_voie,
            String code_postal,
            @NotBlank String code_insee,
            @NotBlank String nom_commune,
            String code_insee_ancienne_commune,
            String nom_ancienne_commune,
            @NotNull Double x,
            @NotNull Double y,
            @NotNull Double lon,
            @NotNull Double lat,
            @NotBlank String type_position,
            String alias,
            String nom_ld,
            String libelle_acheminement,
            String nom_afnor,
            @NotBlank String source_position,
            @NotBlank String source_nom_voie,
            @NotNull Integer certification_commune,
            String cad_parcelles
    ) {
        public boolean isBetterThan(Adresse other) {

            // Règle 1
            if (!Objects.equals(certification_commune, other.certification_commune)) {
                return certification_commune == 1;
            }

            // Règle 2
            if (!Objects.equals(source_nom_voie, other.source_nom_voie)) {
                boolean thisUnknown =
                        "inconnue".equalsIgnoreCase(source_nom_voie);

                boolean otherUnknown =
                        "inconnue".equalsIgnoreCase(other.source_nom_voie);

                if (thisUnknown != otherUnknown) {
                    return !thisUnknown;
                }
            }

            // Règle 3
            if (!Objects.equals(source_position, other.source_position)) {
                boolean thisUnknown =
                        "inconnue".equalsIgnoreCase(source_position);

                boolean otherUnknown =
                        "inconnue".equalsIgnoreCase(other.source_position);

                if (thisUnknown != otherUnknown) {
                    return !thisUnknown;
                }
            }
            return false;
        }
    }

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
                        )
                        ON CONFLICT(id, type_position, x, y)
                        DO UPDATE SET
                            id_fantoir = excluded.id_fantoir,
                            numero = excluded.numero,
                            rep = excluded.rep,
                            nom_voie = excluded.nom_voie,
                            code_postal = excluded.code_postal,
                            code_insee = excluded.code_insee,
                            nom_commune = excluded.nom_commune,
                            code_insee_ancienne_commune = excluded.code_insee_ancienne_commune,
                            nom_ancienne_commune = excluded.nom_ancienne_commune,
                            lon = excluded.lon,
                            lat = excluded.lat,
                            alias = excluded.alias,
                            nom_ld = excluded.nom_ld,
                            libelle_acheminement = excluded.libelle_acheminement,
                            nom_afnor = excluded.nom_afnor,
                            source_position = excluded.source_position,
                            source_nom_voie = excluded.source_nom_voie,
                            certification_commune = excluded.certification_commune,
                            cad_parcelles = excluded.cad_parcelles ;
                       
                        """) // :paramName -> getter du bean
                .beanMapped()
                .assertUpdates(true)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Adresse> stagingWriter(
            DataSource ds) {

        return new JdbcBatchItemWriterBuilder<Adresse>()
                .dataSource(ds)
                .sql("""
                INSERT INTO adresse_staging (
                    id, id_fantoir, numero, rep, nom_voie,
                    code_postal, code_insee, nom_commune,
                    code_insee_ancienne_commune,
                    nom_ancienne_commune,
                    x, y, lon, lat,
                    type_position,
                    alias, nom_ld,
                    libelle_acheminement,
                    nom_afnor,
                    source_position,
                    source_nom_voie,
                    certification_commune,
                    cad_parcelles
                ) VALUES (
                    :id, :id_fantoir, :numero, :rep, :nom_voie,
                    :code_postal, :code_insee, :nom_commune,
                    :code_insee_ancienne_commune,
                    :nom_ancienne_commune,
                    :x, :y, :lon, :lat,
                    :type_position,
                    :alias, :nom_ld,
                    :libelle_acheminement,
                    :nom_afnor,
                    :source_position,
                    :source_nom_voie,
                    :certification_commune,
                    :cad_parcelles
                )
            """)
                .beanMapped()
                .build();
    }

    @Bean
    public BeanValidatingItemProcessor<Adresse> beanValidatingProcessor() {
        BeanValidatingItemProcessor<Adresse> processorBeanV =
                new BeanValidatingItemProcessor<>();
        processorBeanV.setFilter(true);
        return processorBeanV;
    }

    @Bean
    public Step suppressionObsoleteStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            Tasklet suppressionObsoleteTasklet) {

        return new StepBuilder(
                "suppressionObsoleteStep",
                jobRepository)
                .tasklet(
                        suppressionObsoleteTasklet,
                        transactionManager)
                .build();
    }

    @Bean
    public ValidatingItemProcessor<Adresse> validatingProcessor(
            AdresseValidator validator) {

        return new ValidatingItemProcessor<>(validator);
    }


    // CompositeItemProcessor pour le step importAdresseStep
    @Bean
    public CompositeItemProcessor<Adresse, Adresse> compositeProcessor(
            DuplicateRulesProcessor duplicateRulesProcessor,
            ValidatingItemProcessor<Adresse> validatingProcessor,
            BeanValidatingItemProcessor<Adresse> beanValidatingProcessor) {

        CompositeItemProcessor<Adresse, Adresse> processorComp =
                new CompositeItemProcessor<>();

        processorComp.setDelegates(List.of(
                beanValidatingProcessor,
                duplicateRulesProcessor,
                validatingProcessor
        ));

        return processorComp;
    }

    // CompositeItemProcessor pour le step importCsvStep
    @Bean
    public CompositeItemProcessor<Adresse, Adresse> compositeCsvProcessor(
            ValidatingItemProcessor<Adresse> validatingProcessor,
            BeanValidatingItemProcessor<Adresse> beanValidatingProcessor) {

        CompositeItemProcessor<Adresse, Adresse> processorComp =
                new CompositeItemProcessor<>();

        processorComp.setDelegates(List.of(
                beanValidatingProcessor,
                validatingProcessor
        ));

        return processorComp;
    }


    // JobRepository et PlatformTransactionManager sont auto-câblés par Spring Boot
    @Bean
    public Job helloWorldJob(JobRepository jobRepository, Step helloStep) {
        return new JobBuilder("helloWorldJob", jobRepository).start(helloStep).build();
    }

    @Bean
    public Job importAdresseJob(JobRepository jobRepository, Step importAdresseStep,
                                BilanJobListener listener,
                                DuplicationJobListener duplicationListener,
                                Step suppressionObsoleteStep,
                                Step importCsvStep) {
        return new JobBuilder("importAdresseJob", jobRepository)
                .listener(listener)
                .listener(duplicationListener)
                .start(importCsvStep)
                //.start(importAdresseStep)
                //.next(suppressionObsoleteStep)
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
            CompositeItemProcessor <Adresse, Adresse> compositeProcessor,
            StepProgessListener listener,
            AdresseSkipListener skipListener) {
        return new StepBuilder("importAdresseStep", repo)
                .<Adresse, Adresse>chunk(1000)
                .transactionManager(tx)
                .reader(csvReader)
                .processor(compositeProcessor)
                .writer(jdbcWriter)
                .faultTolerant()
                .skip(ValidationException.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(listener)
                .listener(skipListener)
                .build();

    }
    @Bean
    public Step importCsvStep(
            JobRepository repo,
            PlatformTransactionManager tx,
            FlatFileItemReader<Adresse> csvReader,
            JdbcBatchItemWriter<Adresse> stagingWriter,
            CompositeItemProcessor <Adresse, Adresse> compositeCsvProcessor,
            StepProgessListener listener,
            AdresseSkipListener skipListener) {
        return new StepBuilder("importCsvStep", repo)
                .<Adresse, Adresse>chunk(10000)
                .transactionManager(tx)
                .reader(csvReader)
                //.processor(compositeCsvProcessor)
                .writer(stagingWriter)
                .faultTolerant()
                .skip(ValidationException.class)
                .skipLimit(Integer.MAX_VALUE)
                //.listener(listener)
                //.listener(skipListener)
                .build();
    }
}
